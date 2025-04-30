package no.nav.pam.stilling.feed.admin.konsument

import kotlinx.html.*

fun FlowContent.KonsumentTabell(konsumenter: List<KonsumentDTO>) {
    table {
        tr {
            th { +"ID" }
            th { +"Identifikator" }
            th { +"Epost" }
            th { +"Telefon" }
            th { +"Kontaktperson" }
            th { +"Opprettet" }
        }
        konsumenter.forEach { konsument ->
            tr {
                td { +konsument.id.toString() }
                td { +konsument.identifikator }
                td { +konsument.email }
                td { +konsument.telefon }
                td { +konsument.kontaktperson }
                td { +konsument.opprettet.toString() }
            }
        }
    }
}
