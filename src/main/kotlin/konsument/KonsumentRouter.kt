package no.nav.pam.stilling.feed.admin.konsument

import io.javalin.Javalin
import io.javalin.http.Context
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class KonsumentRouter(
    private val konsumentService: KonsumentService
) {
    companion object {
        private val log: Logger = LoggerFactory.getLogger(KonsumentRouter::class.java)
    }

    fun setupRoutes(javalin: Javalin) {
        javalin.post("/konsument/opprett") { håndterOpprettKonsument(it) }
        javalin.get("/konsument/form") { håndterKonsumentForm(it) }
        javalin.get("/konsument/sok") { håndterFinnKonsument(it) }
    }

    val konsumenter = listOf(
        KonsumentDTO(
            UUID.randomUUID(),
            "Tasteriet Trykkeservice",
            "test@example.com",
            "12345678",
            "Karl Q. Lator",
            null
        ),
        KonsumentDTO(UUID.randomUUID(), "Kvernebryggeriet", "kvern@epost.no", "12345678", "Karoline Kvern", null),
        KonsumentDTO(UUID.randomUUID(), "Bakeriet Boller & Brød", "baker@boller.no", "98765432", "Bente Bolle", null),
        KonsumentDTO(UUID.randomUUID(), "Sykkelverkstedet AS", "sykkel@verksted.no", "45678912", "Sindre Sykkel", null),
        KonsumentDTO(UUID.randomUUID(), "Kaffekoppen Kafé", "kaffe@kopp.no", "11223344", "Kari Kopp", null),
        KonsumentDTO(UUID.randomUUID(), "Blomsterbutikken Flora", "flora@blomst.no", "99887766", "Flora Blomst", null),
        KonsumentDTO(
            UUID.randomUUID(),
            "Elektronikk Eksperten",
            "ekspert@elektronikk.no",
            "33445566",
            "Eirik Elektron",
            null
        ),
        KonsumentDTO(UUID.randomUUID(), "Møbelhuset Komfort", "komfort@mobel.no", "77889900", "Mona Møbel", null)
    )

    private fun håndterFinnKonsument(ctx: Context) {
        var spørring = ctx.queryParam("q")
        log.info("Mottar søk etter konsument med spørring: $spørring, ${ctx.body()}")
        if (spørring.isNullOrBlank()) {
            ctx.html(createHTML().span {
                KonsumentTabell(konsumenter)
            })
        }

        ctx.html(createHTML().div {
            id = "konsumentTabell"
            KonsumentTabell(konsumenter.filter { konsument ->
                listOf(
                    konsument.id.toString(),
                    konsument.identifikator,
                    konsument.email,
                    konsument.telefon,
                    konsument.kontaktperson,
                    konsument.opprettet?.toString()
                ).any { it?.contains(spørring ?: "", ignoreCase = true) == true }
            })
        })

    }

    private fun håndterKonsumentForm(ctx: Context) {
        ctx.html(createHTML().div {
            id = "konsument"
            KonsumentForm()
        })
    }

    private fun håndterOpprettKonsument(ctx: Context) {
        val request = KonsumentDTO.fraHtmlForm(ctx.formParamMap())
        val konsument = konsumentService.opprettKonsument(request)
        ctx.html(createHTML().div {
            id = "konsument"
            pre {
                code {
                    +konsument
                }
            }
            button {
                attributes["hx-get"] = "/konsument/form"
                attributes["hx-target"] = "#konsument"
                attributes["hx-swap"] = "outerHTML"
                +"Opprett ny konsument"
            }
        }
        )
    }
}
