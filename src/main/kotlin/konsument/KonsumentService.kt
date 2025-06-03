package no.nav.pam.stilling.feed.admin.konsument

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.pam.stilling.feed.admin.StillingFeedKlient

open class KonsumentService(
    private val stillingFeedKlient: StillingFeedKlient,
    private val objectMapper: ObjectMapper,
) {
    fun opprettKonsument(konsumentDTO: KonsumentDTO): KonsumentDTO {
        val response = stillingFeedKlient.opprettKonsument(konsumentDTO)
        return objectMapper.readValue(response)
    }

    open fun hentKonsumenter(spørring: String?): List<KonsumentDTO> {
        return stillingFeedKlient.hentKonsumenter(spørring ?: "")
    }
}
