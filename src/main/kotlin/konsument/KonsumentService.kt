package no.nav.pam.stilling.feed.admin.konsument

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.pam.stilling.feed.admin.StillingFeedKlient

open class KonsumentService(
    private val stillingFeedKlient: StillingFeedKlient,
    private val objectMapper: ObjectMapper,
) {
    fun opprettKonsument(konsumentDTO: KonsumentDTO): String {
        return stillingFeedKlient.opprettKonsument(konsumentDTO)
    }

    open fun hentKonsumenter(spørring: String?): List<KonsumentDTO> {
        return stillingFeedKlient.hentKonsumenter(spørring ?: "")
    }
}
