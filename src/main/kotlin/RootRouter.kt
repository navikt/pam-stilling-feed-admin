package no.nav.pam.stilling.feed.admin

import io.javalin.Javalin
import io.javalin.http.Context
import kotlinx.html.*
import no.nav.pam.stilling.feed.admin.komponenter.Input
import no.nav.pam.stilling.feed.admin.konsument.KonsumentForm
import no.nav.pam.stilling.feed.admin.token.GenererTokenForm

class RootRouter {
    fun setupRoutes(javalin: Javalin) {
        javalin.get("/") { it.redirect("/konsument") }
        javalin.get("/konsument/opprett") { opprettKonsument(it) }
        javalin.get("/konsument") { finnKonsument(it) }
        javalin.get("/token/generer") { genererToken(it) }
    }

    private fun opprettKonsument(ctx: Context) {
        ctx.html(indexHTML {
            div {
                id = "konsumentForm"
                KonsumentForm()
            }
        })
    }

    private fun finnKonsument(ctx: Context) {
        ctx.html(indexHTML {
                Input {
                    this.id = "konsumentSok"
                    this.name = "q"
                    type = InputType.text
                    autoFocus = true
                    attributes["hx-get"] = "/konsument/sok"
                    attributes["hx-target"] = "#konsumentTabell"
                    attributes["hx-trigger"] = "load, input changed delay:250ms"
                    attributes["hx-swap"] = "outerHTML"
                    placeholder = "Søk etter konsument"
                }

            div { id = "konsumentTabell" }
        })
    }

    private fun genererToken(ctx: Context) {
        ctx.html(indexHTML {
            GenererTokenForm()
        })
    }
}
