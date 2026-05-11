package no.nav.pam.stilling.feed.admin.token

class Secret(val identifier: String, val key: String)
class Record(val secret: Secret)

data class OneTimeSecretDTO(
    val success: Boolean,
    val record: Record
)
