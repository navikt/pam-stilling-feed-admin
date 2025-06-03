package token

import app.TestRunningApplication
import io.javalin.http.ContentType
import io.javalin.http.HttpStatus
import io.mockk.every
import it.skrape.core.*
import it.skrape.fetcher.*
import it.skrape.selects.*
import konsument.genererTilfeldig
import no.nav.pam.stilling.feed.admin.konsument.KonsumentDTO
import no.nav.pam.stilling.feed.admin.token.TokenRequestDTO
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.net.URI
import java.time.LocalDateTime
import java.util.UUID

fun TokenRequestDTO.Companion.genererTilfeldig(): TokenRequestDTO {
    return TokenRequestDTO(
        konsumentId = UUID.randomUUID(),
        expires = LocalDateTime.now().plusDays(30)
    )
}

class TokenRouterTest : TestRunningApplication() {
    val tokenService = appCtx.tokenServiceMock
    val konsumentService = appCtx.konsumentServiceMock

    @Test
    fun `Skal laste siden for 'generer token'`() {
        val konsumenter = List(10) { KonsumentDTO.genererTilfeldig() }

        every { konsumentService.hentKonsumenter(any()) } returns konsumenter

        skrape(HttpFetcher) {
            request { url = "$lokalBaseUrl/token/form" }
            response {
                status { assertThat(code).isEqualTo(200) }
                assertThat(contentType).contains(ContentType.TEXT_HTML.mimeType)

                htmlDocument {
                    findAll("h2") {
                        assertThat(size).isEqualTo(1)
                        assertThat(eachTagName).isEqualTo(listOf("h2"))
                        assertThat(text).isEqualTo("Generer token")
                    }

                    findAll("p") {
                        assertThat(size).isEqualTo(1)
                        assertThat(eachTagName).isEqualTo(listOf("p"))
                        assertThat(text).isEqualTo("Velg konsument for å generere et token og en utfylt e-post.")
                    }

                    findAll("form") {
                        assertThat(size).isEqualTo(1)
                        assertThat(first().attributes["hx-post"]).isEqualTo("/token/generer")
                        assertThat(first().attributes["hx-target"]).isEqualTo("#genererToken")
                        assertThat(first().attributes["hx-swap"]).isEqualTo("outerHTML")
                        assertThat(first().attributes["hx-indicator"]).isEqualTo("#laster")

                        findAll("label") {
                            assertThat(size).isEqualTo(3)
                            assertThat(map { it.text }).containsExactly(
                                "Velg konsument:",
                                "Utløpsdato: (La være tom for ingen utløpsdato)",
                                "Generer token"
                            )
                        }

                        findAll("select") {
                            assertThat(size).isEqualTo(1)
                            assertThat(first().attributes["name"]).isEqualTo("konsumentId")

                            findAll("option") {
                                assertThat(size).isEqualTo(konsumenter.size)
                                konsumenter.forEachIndexed { index, konsument ->
                                    assertThat(this[index].attributes["value"]).isEqualTo(konsument.id.toString())
                                    assertThat(this[index].text).isEqualTo(konsument.identifikator)
                                }
                            }
                        }

                        findAll("input[name=expires]") {
                            assertThat(size).isEqualTo(1)
                            assertThat(first().attributes["name"]).isEqualTo("expires")
                            assertThat(first().attributes["type"]).isEqualTo("datetime-local")
                            assertThat(first().attributes["required"]).isNull()
                        }

                        findAll("button") {
                            assertThat(size).isEqualTo(1)
                            assertThat(first().attributes["type"]).isEqualTo("button")
                        }
                    }

                    findAll("#laster") {
                        assertThat(size).isEqualTo(1)
                        assertThat(first().tagName).isEqualTo("div")
                        assertThat(first().attributes["style"]).contains("display: none;")
                    }
                }
            }
        }
    }

    @Test
    fun `Skal generere token og e-post mal`() {
        val tokenRequest = TokenRequestDTO.genererTilfeldig()
        val konsument = KonsumentDTO.genererTilfeldig().copy(id = tokenRequest.konsumentId)
        val token = "Noe beskrivende tekst her: Bearer 1234567890abcdef"

        every { konsumentService.hentKonsumenter(tokenRequest.konsumentId.toString()) } returns listOf(konsument)
        every { tokenService.genererToken(tokenRequest) } returns token
        every { tokenService.genererOneTimeSecret(any()) } returns URI.create("https://example.com/one-time-secret")
        every { tokenService.oneTimeSecretPassphrase } returns "passphrase"

        skrape(HttpFetcher) {
            request {
                url = "$lokalBaseUrl/token/generer"
                method = Method.POST
                body {
                    form {
                        "konsumentId" to tokenRequest.konsumentId.toString()
                        "expires" to tokenRequest.expires.toString()
                    }
                }
            }
            response {
                status { assertThat(code).isEqualTo(HttpStatus.OK.code) }
                assertThat(contentType).contains(ContentType.TEXT_HTML.mimeType)
                htmlDocument {
                    findAll("h2") {
                        assertThat(size).isEqualTo(1)
                        assertThat(text).isEqualTo("Token for konsument: ${konsument.identifikator}")
                    }

                    findAll("p") {
                        assertThat(size).isEqualTo(1)
                        assertThat(text).isEqualTo("Utløpsdato: ${tokenRequest.expires}")
                    }

                    findAll("label") {
                        assertThat(size).isEqualTo(2)
                        assertThat(map { it.text }).containsExactly(
                            "Kopier token",
                            "Kopier e-postmal"
                        )
                    }

                    findAll("input") {
                        assertThat(size).isEqualTo(1)
                        assertThat(first().attributes["id"]).isEqualTo("token")
                        assertThat(first().attributes["value"]).isEqualTo(token.split("Bearer ")[1])
                        assertThat(first().attributes["disabled"]).isEqualTo(true.toString())
                    }

                    findAll("textarea") {
                        assertThat(size).isEqualTo(1)
                        assertThat(first().attributes["id"]).isEqualTo("epostmal")
                        assertThat(first().text).contains("https://example.com/one-time-secret")
                        assertThat(first().text).contains("passphrase")
                    }
                }
            }
        }
    }

    @Test
    fun `Skal vise error-siden ved feil i generering av token`() {
        val feilmelding = "Feil ved generering av token"

        every { tokenService.genererToken(any()) } throws RuntimeException(feilmelding)

        skrape(HttpFetcher) {
            request {
                url = "$lokalBaseUrl/token/generer"
                method = Method.POST
                body {
                    form {
                        "konsumentId" to UUID.randomUUID().toString()
                        "expires" to LocalDateTime.now().toString()
                    }
                }
            }
            response {
                status { assertThat(code).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.code) }
                assertThat(contentType).contains(ContentType.TEXT_HTML.mimeType)
                htmlDocument {
                    findAll("h2") {
                        assertThat(size).isEqualTo(1)
                        assertThat(text).isEqualTo("Åisann, det skjedde visst noe feil!")
                    }

                    findAll("pre") {
                        assertThat(size).isEqualTo(1)
                        assertThat(text).contains(feilmelding)
                    }
                }
            }
        }
    }
}
