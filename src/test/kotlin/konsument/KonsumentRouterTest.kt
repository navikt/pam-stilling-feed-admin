package konsument

import app.TestRunningApplication
import io.javalin.http.ContentType
import io.javalin.http.HttpStatus
import io.mockk.every
import it.skrape.core.htmlDocument
import it.skrape.fetcher.*
import it.skrape.selects.*
import no.nav.pam.stilling.feed.admin.konsument.KonsumentDTO
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.net.URI
import java.time.LocalDateTime
import java.util.*

fun KonsumentDTO.Companion.genererTilfeldig(): KonsumentDTO {
    return KonsumentDTO(
        id = UUID.randomUUID(),
        identifikator = "identifikator-${UUID.randomUUID()}",
        email = "test-${UUID.randomUUID()}@example.com",
        telefon = "0047${Random().nextInt(10000000, 99999999)}",
        kontaktperson = "Kontaktperson ${UUID.randomUUID()}",
        opprettet = LocalDateTime.now()
    )
}

class KonsumentRouterTest : TestRunningApplication() {
    val konsumentService = appCtx.konsumentServiceMock

    @Test
    fun `Skal laste siden for 'finn konsument'`() {
        skrape(HttpFetcher) {
            request { url = "$lokalBaseUrl/konsument/sok" }
            response {
                status { assertThat(code).isEqualTo(HttpStatus.OK.code) }
                assertThat(contentType).contains(ContentType.TEXT_HTML.mimeType)
                htmlDocument {
                    findAll("h2") {
                        assertThat(size).isEqualTo(1)
                        assertThat(eachTagName).isEqualTo(listOf("h2"))
                        assertThat(text).isEqualTo("Søk etter konsument")
                    }

                    findAll("input") {
                        assertThat(size).isEqualTo(1)
                        assertThat(eachTagName).isEqualTo(listOf("input"))
                        assertThat(first().id).isEqualTo("konsumentSok")
                        assertThat(first().attributes["hx-get"]).isEqualTo("/konsument/tabell")
                        assertThat(first().attributes["hx-target"]).isEqualTo("#konsumentTabell")
                    }

                    findAll("#konsumentTabell") {
                        assertThat(size).isEqualTo(1)
                        assertThat(first().tagName).isEqualTo("div")
                        assertThat(first().html).isEmpty()
                    }
                }
            }
        }
    }

    @Test
    fun `Skal opprette en tabell gitt en liste med konsumenter`() {
        val konsumenter = List(10) { KonsumentDTO.genererTilfeldig() }

        every { konsumentService.hentKonsumenter(any()) } returns konsumenter

        skrape(HttpFetcher) {
            request { url = "$lokalBaseUrl/konsument/tabell" }
            response {
                status { assertThat(code).isEqualTo(HttpStatus.OK.code) }
                assertThat(contentType).contains(ContentType.TEXT_HTML.mimeType)
                htmlDocument {
                    findAll("p") {
                        assertThat(size).isEqualTo(1)
                        assertThat(text).isEqualTo("${konsumenter.size} konsumenter")
                    }

                    findAll("table") {
                        assertThat(size).isEqualTo(1)
                        assertThat(first().tagName).isEqualTo("table")

                        findAll("thead") {
                            assertThat(size).isEqualTo(1)
                            assertThat(first().children.first().tagName).isEqualTo("tr")
                            val tr = first().children.first()
                            assertThat(tr.children.map { it.text }).containsExactly(
                                "ID", "Identifikator", "E-post", "Telefon", "Kontaktperson", "Opprettet"
                            )
                        }

                        findAll("tbody") {
                            assertThat(size).isEqualTo(1)
                            assertThat(first().children.size).isEqualTo(konsumenter.size)

                            konsumenter.forEachIndexed { index, konsument ->
                                val tr = first().children[index]
                                assertThat(tr.tagName).isEqualTo("tr")
                                assertThat(tr.children.map { it.text }).containsExactly(
                                    konsument.id.toString(),
                                    konsument.identifikator,
                                    konsument.email,
                                    konsument.telefon,
                                    konsument.kontaktperson,
                                    konsument.opprettet.toString()
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Skal laste siden for konsument skjemaet`() {
        skrape(HttpFetcher) {
            request { url = "$lokalBaseUrl/konsument/form" }
            response {
                status { assertThat(code).isEqualTo(HttpStatus.OK.code) }
                assertThat(contentType).contains(ContentType.TEXT_HTML.mimeType)
                htmlDocument {
                    findAll("h2") {
                        assertThat(size).isEqualTo(1)
                        assertThat(text).isEqualTo("Opprett konsument")
                    }

                    findAll("p") {
                        assertThat(size).isEqualTo(1)
                        assertThat(text).isEqualTo("Fyll ut skjema for å opprette en ny konsument.")
                    }

                    findAll("form") {
                        assertThat(size).isEqualTo(1)

                        findAll("input") { assertThat(size).isEqualTo(6) }
                        findAll("button") { assertThat(size).isEqualTo(1) }
                        findAll("label") {
                            assertThat(size).isEqualTo(5)
                            println(html)
                            assertThat(map { it.text }).containsExactly(
                                "Identifikator:", "E-post:", "Telefon:", "Kontaktperson:", "Legg til konsument"
                            )
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Skal opprette en konsument via skjemaet`() {
        val konsument = KonsumentDTO.genererTilfeldig()

        every { konsumentService.opprettKonsument(any()) } returns konsument
        every { appCtx.tokenServiceMock.genererToken(any()) } returns "Bearer ${UUID.randomUUID()}"
        every { appCtx.tokenServiceMock.genererOneTimeSecret(any()) } returns URI.create("https://example.com/one-time-secret")
        every { appCtx.tokenServiceMock.oneTimeSecretPassphrase } returns "passphrase"

        skrape(HttpFetcher) {
            request {
                url = "$lokalBaseUrl/konsument/opprett"
                method = Method.POST
                body {
                    form {
                        "identifikator" to konsument.identifikator
                        "email" to konsument.email
                        "telefon" to konsument.telefon
                        "kontaktperson" to konsument.kontaktperson
                    }
                }
            }
            response {
                status { assertThat(code).isEqualTo(HttpStatus.OK.code) }
                assertThat(contentType).contains(ContentType.TEXT_HTML.mimeType)
                htmlDocument {
                    findAll("h2") {
                        assertThat(size).isEqualTo(1)
                        assertThat(text).contains("Token for konsument:")
                    }

                    findAll("button") {
                        assertThat(size).isEqualTo(2)
                        assertThat(text).contains("Kopier")
                    }
                }
            }
        }
    }
}
