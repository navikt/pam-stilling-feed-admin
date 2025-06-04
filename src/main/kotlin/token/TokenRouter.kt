package no.nav.pam.stilling.feed.admin.token

import io.javalin.Javalin
import io.javalin.http.Context
import io.javalin.http.HttpStatus
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import no.nav.pam.stilling.feed.admin.createFragmentHTML
import no.nav.pam.stilling.feed.admin.fragment
import no.nav.pam.stilling.feed.admin.komponenter.*
import no.nav.pam.stilling.feed.admin.konsument.KonsumentService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class TokenRouter(
    private val tokenService: TokenService,
    private val konsumentService: KonsumentService,
) {
    companion object {
        val log: Logger = LoggerFactory.getLogger(TokenRouter::class.java)
    }

    fun setupRoutes(javalin: Javalin) {
        javalin.get("/token/form") { lastInnTokenForm(it) }
        javalin.post("/token/generer") { hånterGenererToken(it) }
    }

    private fun lastInnTokenForm(ctx: Context) {
        ctx.html(createFragmentHTML().fragment {
            h2 { +"Generer token" }

            p { +"Velg konsument for å generere et nytt token og en ferdig utfylt e-post." }

            GenererTokenForm(konsumentService.hentKonsumenter(""))

            Laster()
        })
    }

    private fun hånterGenererToken(ctx: Context) = try {
        log.info("Mottokk request: ${ctx.body()}")
        log.info("Håndterer token request for konsumentId: ${ctx.formParam("konsumentId")}")
        val tokenRequest = TokenRequestDTO.fraHtmlForm(ctx.formParamMap())
        val konsument = konsumentService.hentKonsumenter(tokenRequest.konsumentId.toString()).first()
        val token = tokenService.genererToken(tokenRequest).split("Bearer ")[1]
        tokenService.genererOneTimeSecret(token)

        ctx.html(createHTML().section {
            style = """
                display: flex;
                flex-direction: column;
                gap: 1rem;
            """.trimIndent()

            h2 { +"Token for konsument: ${konsument.identifikator}" }

            p { +"Utløpsdato: ${tokenRequest.expires ?: "Ingen utløpsdato"}" }

            Input {
                id = "token"
                value = token
                attributes["disabled"] = true.toString()
                attributes["style"] = "width: 100%;"
            }

            Button {
                val standardLabel = "Kopier token"
                val kopiertLabel = "\u2713 Kopiert!"
                id = "kopierToken"
                type = ButtonType.button
                variant = ButtonVariant.PRIMARY
                attributes["_"] = "on click queue last set :token to #token.value " +
                        "then call the window's navigator's clipboard's writeText(:token) " +
                        "then set my.firstChild.textContent to '$kopiertLabel' " +
                        "then wait 1 seconds then set my.firstChild.textContent to '$standardLabel'"
                label = standardLabel
            }

            EpostMal {
                oneTimeSecretUrl = tokenService.genererOneTimeSecret(token)
                passphrase = tokenService.oneTimeSecretPassphrase
            }
        })
    } catch (e: Exception) {
        log.error("Feil ved generering av token", e)
        ctx.status(HttpStatus.INTERNAL_SERVER_ERROR)
        ctx.html(createHTML().section {
            h2 { +"Åisann, det skjedde visst noe feil!" }

            pre {
                +"Feil ved generering av token"
                details {
                    summary { +"Caused by: ${e.cause}: ${e.message}" }
                    +e.stackTraceToString()
                }
            }
        })
    }
}
