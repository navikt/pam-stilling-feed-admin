package no.nav.pam.stilling.feed.admin.token

class Metadata(val identifier: String, val key: String)
class Secret(val identifier: String, val key: String)
class Record(val metadata: Metadata, val secret: Secret)

data class OneTimeSecretDTO(
    val success: Boolean,
    val shrimp: String,
    val custid: String,
    val record: Record,
)

class MetadataRecord(val identifier: String, val key: String, val share_url: String)

data class OneTimeSecretMetadataDTO(
    val success: Boolean,
    val record: MetadataRecord,
)
