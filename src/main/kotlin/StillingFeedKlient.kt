package no.nav.pam.stilling.feed.admin

import no.nav.pam.stilling.feed.admin.konsument.KonsumentDTO
import no.nav.pam.stilling.feed.admin.token.TokenRequestDTO
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse.BodyHandlers
import java.util.*

class StillingFeedKlient(
    private val stillingFeedBaseUrl: String,
    private val stillingFeedToken: String,
    private val httpClient: HttpClient,
) {
    companion object {
        val log: Logger = LoggerFactory.getLogger(StillingFeedKlient::class.java)
    }
    fun opprettKonsument(konsument: KonsumentDTO): String {
        val response = sendPostRequest("/internal/api/newConsumer", """
            {
                "identifikator": "${konsument.identifikator}",
                "email": "${konsument.email}",
                "telefon": "${konsument.telefon}",
                "kontaktperson": "${konsument.kontaktperson}"
            }
        """.trimIndent())
        when (response.statusCode()) {
            200 -> {
                log.info("Konsument opprettet: ${response.body()}")
                return response.body()
            }
            400 -> {
                log.error("Feil ved opprettelse av konsument: ${response.body()}")
                throw IllegalArgumentException("Feil ved opprettelse av konsument: ${response.body()}")
            }
            else -> {
                log.error("Uventet respons ved opprettelse av konsument: ${response.statusCode()} - ${response.body()}")
                throw RuntimeException("Uventet respons ved opprettelse av konsument: ${response.statusCode()} - ${response.body()}")
            }
        }
    }

    fun genererToken(tokenRequest: TokenRequestDTO): String {
        log.info("Motar tokenRequest: $tokenRequest")
        val response = sendPostRequest("/internal/api/newApiToken", """
            {
                "konsumentId": "${tokenRequest.konsumentId}",
                "expires": "${if (tokenRequest.expires != null) tokenRequest.expires.toString() else ""}"
            }
        """.trimIndent())
        when (response.statusCode()) {
            200 -> {
                log.info("Token generert: ${response.body()}")
                return response.body()
            }
            400 -> {
                log.error("Feil ved generering av token: ${response.body()}")
                throw IllegalArgumentException("Feil ved generering av token: ${response.body()}")
            }
            else -> {
                log.error("Uventet respons ved generering av token: ${response.statusCode()} - ${response.body()}")
                throw RuntimeException("Uventet respons ved generering av token: ${response.statusCode()} - ${response.body()}")
            }
        }
    }

    private fun sendPostRequest(endepunkt: String, body: String, token: String = stillingFeedToken) = httpClient.send(
        HttpRequest.newBuilder()
            .uri(URI("$stillingFeedBaseUrl$endepunkt"))
            .POST(BodyPublishers.ofString(body))
            .setHeader("Authorization", "Bearer $token")
            .build(),
        BodyHandlers.ofString()
    )
}
