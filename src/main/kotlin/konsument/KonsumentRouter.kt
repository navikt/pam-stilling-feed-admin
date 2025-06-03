package no.nav.pam.stilling.feed.admin.konsument

import io.javalin.Javalin
import io.javalin.http.Context
import kotlinx.html.*
import no.nav.pam.stilling.feed.admin.createFragmentHTML
import no.nav.pam.stilling.feed.admin.fragment
import no.nav.pam.stilling.feed.admin.komponenter.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse.BodyHandlers

class KonsumentRouter(
    private val konsumentService: KonsumentService,
    private val httpClient: HttpClient,
    private val baseUrl: String,
) {
    companion object {
        private val log: Logger = LoggerFactory.getLogger(KonsumentRouter::class.java)
    }

    fun setupRoutes(javalin: Javalin) {
        javalin.get("/konsument/sok") { lastInnFinnKonsument(it) }
        javalin.get("/konsument/tabell") { håndterKonsumentTabell(it) }
        javalin.get("/konsument/form") { lastInnKonsumentForm(it) }
        javalin.post("/konsument/opprett") { håndterOpprettKonsument(it) }
    }

    private fun lastInnFinnKonsument(ctx: Context) {
        ctx.html(createFragmentHTML().fragment {
            h2 {
                style = "text-align: center;"
                +"Søk etter konsument"
            }

            Input {
                attributes["style"] = """
                    margin: 2rem 0 4rem 0;
                    max-width: 800px;
                    min-width: 70%;
                """.trimIndent()
                id = "konsumentSok"
                name = "q"
                type = InputType.text
                autoFocus = true
                attributes["hx-get"] = "/konsument/tabell"
                attributes["hx-target"] = "#konsumentTabell"
                attributes["hx-trigger"] = "load, input changed delay:250ms"
                placeholder = "ID, Identifikator, E-post, Telefon eller Kontaktperson"
            }

            div { id = "konsumentTabell" }
        })
    }

    private fun håndterKonsumentTabell(ctx: Context) {
        val spørring = ctx.queryParam("q")
        log.info("Mottar søk etter konsument med spørring: $spørring")
        val konsumenter = konsumentService.hentKonsumenter(spørring)
        ctx.html(createFragmentHTML().fragment {
            p { +"${konsumenter.size} ${if (konsumenter.size == 1) "konsument" else "konsumenter"}" }

            table {
                thead {
                    tr {
                        th { +"ID" }
                        th { +"Identifikator" }
                        th { +"E-post" }
                        th { +"Telefon" }
                        th { +"Kontaktperson" }
                        th { +"Opprettet" }
                    }
                }
                tbody {
                    this@table.KonsumentTabell(konsumenter)
                }
            }
        })
    }

    private fun lastInnKonsumentForm(ctx: Context) {
        ctx.html(createFragmentHTML().fragment {
            h2 { +"Opprett konsument" }

            p { +"Fyll ut skjema for å opprette en ny konsument." }

            KonsumentForm()

            Laster()
        })
    }

    private fun håndterOpprettKonsument(ctx: Context) {
        val request = KonsumentDTO.fraHtmlForm(ctx.formParamMap())
        val konsument = konsumentService.opprettKonsument(request)
        val html = httpClient.send(
            HttpRequest.newBuilder()
                .uri(URI.create("$baseUrl/token/generer"))
                .POST(BodyPublishers.ofString("konsumentId=${konsument.id}"))
                .build(),
            BodyHandlers.ofString()
        )
        ctx.html(html.body())
    }
}
