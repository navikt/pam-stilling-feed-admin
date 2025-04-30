package no.nav.pam.stilling.feed.admin.token

import io.javalin.Javalin
import io.javalin.http.Context
import kotlinx.html.*
import kotlinx.html.stream.createHTML
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
        val response = tokenService.genererToken(tokenRequest)
        ctx.html(createHTML().pre {
            code {
                +response
            }
        })
    } catch (e: Exception) {
        ctx.html("Feil ved generering av token: ${e.message}")
    }
}
