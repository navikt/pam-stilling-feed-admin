package no.nav.pam.stilling.feed.admin.token

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

data class TokenRequestDTO(
    val konsumentId: UUID, val expires: LocalDateTime?
) {
    companion object {
        fun fraHtmlForm(form: Map<String, List<String>>): TokenRequestDTO {
            val konsumentId = form["konsumentId"]?.firstOrNull()?.let { UUID.fromString(it) }
                ?: throw IllegalArgumentException("Ugyldig konsumentId")
            val expires = form["expires"]?.firstOrNull()?.takeIf { it.isNotBlank() }?.let {
                LocalDateTime.parse(it, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            }
            return TokenRequestDTO(konsumentId, expires)
        }
    }
}
