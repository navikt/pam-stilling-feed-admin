package no.nav.pam.stilling.feed.admin.konsument

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.pam.stilling.feed.admin.StillingFeedKlient

class KonsumentService(
    private val stillingFeedKlient: StillingFeedKlient,
    private val objectMapper: ObjectMapper,
) {
    fun opprettKonsument(konsumentDTO: KonsumentDTO): String {
        return stillingFeedKlient.opprettKonsument(konsumentDTO)
    }
}
