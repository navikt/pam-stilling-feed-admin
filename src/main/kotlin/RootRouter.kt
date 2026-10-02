package no.nav.pam.stilling.feed.admin

import io.javalin.config.JavalinConfig
import io.javalin.http.Context
import kotlinx.html.*
import no.nav.pam.stilling.feed.admin.komponenter.Modal

class RootRouter {
    fun setupRoutes(config: JavalinConfig) {
        config.routes.get("/") { it.redirect("/konsument") }
        config.routes.get("/konsument/opprett") { opprettKonsument(it) }
        config.routes.get("/konsument") { finnKonsument(it) }
        config.routes.get("/token/generer") { genererToken(it) }
        config.routes.get("/modal") { visModal(it) }
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

    private fun visModal(ctx: Context) = try {
        val heading = requireNotNull(ctx.queryParam("heading")) { "Mangler heading parameter for modal" }
        val body = requireNotNull(ctx.queryParam("body")) { "Mangler body parameter for modal" }
        log.info("Viser modal med heading: $heading og body: $body")
        ctx.html(createFragmentHTML().fragment { Modal {
            this.heading = heading
            this.body = body
        } })
    } catch (e: Exception) {
        ctx.status(500).result("Error rendering modal: ${e.message}")
    }
}
