package no.nav.pam.stilling.feed.admin

import io.javalin.Javalin
import io.javalin.http.Context
import kotlinx.html.*
import no.nav.pam.stilling.feed.admin.komponenter.Input
import no.nav.pam.stilling.feed.admin.konsument.KonsumentForm
import no.nav.pam.stilling.feed.admin.token.GenererTokenForm

class RootRouter(
    private val stillingFeedKlient: StillingFeedKlient
) {
    fun setupRoutes(javalin: Javalin) {
        javalin.get("/") { it.redirect("/konsument") }
        javalin.get("/konsument/opprett") { opprettKonsument(it) }
        javalin.get("/konsument") { finnKonsument(it) }
        javalin.get("/token/generer") { genererToken(it) }
    }

    private fun opprettKonsument(ctx: Context) {
        ctx.html(indexHTML {
            section {
                h2 { +"Opprett konsument" }

                p { +"Fyll ut skjema for å opprette en ny konsument." }

                div {
                    id = "konsumentForm"
                    KonsumentForm()
                }
            }
        })
    }

    private fun finnKonsument(ctx: Context) {
        ctx.html(indexHTML {
            h2 { +"Søk etter konsument" }

            Input {
                this.id = "konsumentSok"
                this.name = "q"
                type = InputType.text
                autoFocus = true
                attributes["hx-get"] = "/konsument/sok"
                attributes["hx-target"] = "#konsumentTabell"
                attributes["hx-trigger"] = "load, input changed delay:250ms"
                attributes["hx-swap"] = "outerHTML"
                placeholder = "ID, Identifikator, Epost, Telefon eller Kontaktperson"
            }

            div { id = "konsumentTabell" }
        })
    }

    private fun genererToken(ctx: Context) {
        ctx.html(indexHTML {
            section {
                id = "genererTokenForm"

                h2 { +"Generer token" }

                p { +"Velg konsument for å generere et token og en utfylt epost." }

                GenererTokenForm(stillingFeedKlient.hentKonsumenter(""))
            }
        })
    }
}
