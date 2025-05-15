package no.nav.pam.stilling.feed.admin.token

import no.nav.pam.stilling.feed.admin.StillingFeedKlient
import java.net.URI

class TokenService(
    private val stillingFeedKlient: StillingFeedKlient,
    private val oneTimeSecretKlient: OneTimeSecretKlient,
) {
    val oneTimeSecretPassphrase = oneTimeSecretKlient.passphrase

    fun genererToken(tokenRequest: TokenRequestDTO): String {
        return stillingFeedKlient.genererToken(tokenRequest)
    }

    fun genererOneTimeSecret(token: String): URI {
        val oneTimeSecret = oneTimeSecretKlient.opprettSecret(token)
        val metadata = oneTimeSecretKlient.hentMetadata(oneTimeSecret.record.metadata.identifier)
        return URI.create(metadata.record.share_url)
    }
}
