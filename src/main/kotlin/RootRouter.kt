package no.nav.pam.stilling.feed.admin

import io.javalin.Javalin
import io.javalin.http.Context
import kotlinx.html.*

class RootRouter {
    fun setupRoutes(javalin: Javalin) {
        javalin.get("/") { it.redirect("/konsument") }
        javalin.get("/konsument/opprett") { opprettKonsument(it) }
        javalin.get("/konsument") { finnKonsument(it) }
        javalin.get("/token/generer") { genererToken(it) }
    }

    private fun opprettKonsument(ctx: Context) {
        ctx.html(indexHTML {
            section {
                id = "konsument"
                attributes["hx-get"] = "/konsument/form"
                attributes["hx-target"] = "#konsument"
                attributes["hx-trigger"] = "load"
            }
        })
    }

    private fun finnKonsument(ctx: Context) {
        ctx.html(indexHTML {
            div {
                id = "konsumentSok"
                attributes["hx-get"] = "/konsument/sok"
                attributes["hx-target"] = "#konsumentSok"
                attributes["hx-trigger"] = "load"
            }
        })
    }

    private fun genererToken(ctx: Context) {
        ctx.html(indexHTML {
            section {
                id = "genererToken"
                attributes["hx-get"] = "/token/form"
                attributes["hx-target"] = "#genererToken"
                attributes["hx-trigger"] = "load"
            }
        })
    }
}
