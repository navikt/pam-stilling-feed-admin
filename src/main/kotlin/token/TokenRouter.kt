package no.nav.pam.stilling.feed.admin.token

import io.javalin.Javalin
import io.javalin.http.Context
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import no.nav.pam.stilling.feed.admin.komponenter.Button
import no.nav.pam.stilling.feed.admin.komponenter.ButtonVariant
import no.nav.pam.stilling.feed.admin.komponenter.EpostMal
import no.nav.pam.stilling.feed.admin.komponenter.Input
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class TokenRouter(
    private val tokenService: TokenService,
) {
    companion object {
        val log: Logger = LoggerFactory.getLogger(TokenRouter::class.java)
    }

    fun setupRoutes(javalin: Javalin) {
        javalin.post("/token/generer") { hånterGenererToken(it) }
    }

    private fun hånterGenererToken(ctx: Context) = try {
        log.info("Håndterer token request for konsumentId: ${ctx.formParam("konsumentId")}")
        val tokenRequest = TokenRequestDTO.fraHtmlForm(ctx.formParamMap())
        val token = tokenService.genererToken(tokenRequest).split("Bearer ")[1]
        tokenService.genererOneTimeSecret(token)

        ctx.html(createHTML().div {
            style = """
                display: flex;
                flex-direction: column;
                gap: 1rem;
            """.trimIndent()

            h2 { +"Token for konsument: ${tokenRequest.konsumentId}" }

            p { +"Utløpsdato: ${tokenRequest.expires ?: "Ingen utløpsdato"}" }

            Input {
                id = "token"
                value = token
                attributes["disabled"] = true.toString()
                attributes["style"] = "width: 100%;"
            }

            Button {
                id = "kopierToken"
                type = ButtonType.button
                variant = ButtonVariant.PRIMARY
                attributes["onClick"] = "kopierTilUtklippstavle('#token', '#kopierToken', 'Kopier token')"
                label = "Kopier token"
            }

            EpostMal {
                oneTimeSecretUrl = tokenService.genererOneTimeSecret(token)
                passphrase = tokenService.oneTimeSecretPassphrase
            }
        })
    } catch (e: Exception) {
        ctx.html("Feil ved generering av token: ${e.message}")
    }
}
