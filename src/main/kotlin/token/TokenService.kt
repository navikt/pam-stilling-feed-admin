package no.nav.pam.stilling.feed.admin.token

import no.nav.pam.stilling.feed.admin.StillingFeedKlient
import java.util.*

class TokenService(
    private val stillingFeedKlient: StillingFeedKlient
) {
    fun genererToken(tokenRequest: TokenRequestDTO): String {
        return stillingFeedKlient.genererToken(tokenRequest)
    }
}
