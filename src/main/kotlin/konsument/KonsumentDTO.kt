package no.nav.pam.stilling.feed.admin.konsument

import java.time.LocalDateTime
import java.util.*

data class KonsumentDTO(
    val id: UUID?,
    val identifikator: String,
    val email: String,
    val telefon: String,
    val kontaktperson: String,
    val opprettet: LocalDateTime?,
) {
    companion object {
        fun fraHtmlForm(form: Map<String, List<String>>): KonsumentDTO {
            return KonsumentDTO(
                id = null,
                identifikator = form["identifikator"]?.first() ?: requireNotNull("Identifikator er påkrevd"),
                email = form["email"]?.first() ?: requireNotNull("Email er påkrevd"),
                telefon = form["telefon"]?.first() ?: requireNotNull("Telefon er påkrevd"),
                kontaktperson = form["kontaktperson"]?.first() ?: requireNotNull("Kontaktperson er påkrevd"),
                opprettet = null,
            )
        }
    }
}
