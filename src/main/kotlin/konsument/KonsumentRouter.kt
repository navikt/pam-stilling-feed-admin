package no.nav.pam.stilling.feed.admin.konsument

import io.javalin.Javalin
import io.javalin.http.Context
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import no.nav.pam.stilling.feed.admin.komponenter.Button
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class KonsumentRouter(
    private val konsumentService: KonsumentService
) {
    companion object {
        private val log: Logger = LoggerFactory.getLogger(KonsumentRouter::class.java)
    }

    fun setupRoutes(javalin: Javalin) {
        javalin.post("/konsument/opprett") { håndterOpprettKonsument(it) }
        javalin.get("/konsument/sok") { håndterFinnKonsument(it) }
    }

    private fun håndterFinnKonsument(ctx: Context) {
        val spørring = ctx.queryParam("q")
        log.info("Mottar søk etter konsument med spørring: $spørring")
        val konsumenter = konsumentService.hentKonsumenter(spørring)
        ctx.html(createHTML().div {
            id = "konsumentTabell"
            style = "width: 100%;"
            div { +"${konsumenter.size} ${if (konsumenter.size == 1) "konsument" else "konsumenter"}" }
            KonsumentTabell(konsumenter)
        })
    }

    private fun håndterOpprettKonsument(ctx: Context) {
        val request = KonsumentDTO.fraHtmlForm(ctx.formParamMap())
        val konsument = konsumentService.opprettKonsument(request)
        ctx.html(createHTML().section {
            id = "konsument"

            h2 { +"Opprettet konsument" }

            pre {
                style = "width: 100%; margin-bottom: 1rem;"
                code {
                    +konsument
                }
            }

            Button {
                attributes["hx-get"] = "/konsument/opprett"
                attributes["hx-target"] = "body"
                attributes["autofocus"] = "true"
                label = "Opprett ny konsument"
            }
        }
        )
    }
}
