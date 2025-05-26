package no.nav.pam.stilling.feed.admin.konsument

import kotlinx.html.*

fun TABLE.KonsumentTabell(konsumenter: List<KonsumentDTO>) {
    konsumenter.forEach { konsument ->
        tr {
            td {
                style = "min-width: 300px;"
                code { +konsument.id.toString() }
            }
            td { +konsument.identifikator }
            td { +konsument.email }
            td {
                style = "min-width: 96px;"
                +konsument.telefon
            }
            td { +konsument.kontaktperson }
            td { +konsument.opprettet.toString() }
        }
    }
}
