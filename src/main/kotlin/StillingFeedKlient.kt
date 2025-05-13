package no.nav.pam.stilling.feed.admin

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.javalin.http.HttpStatus
import no.nav.pam.stilling.feed.admin.konsument.KonsumentDTO
import no.nav.pam.stilling.feed.admin.token.TokenRequestDTO
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse.BodyHandlers

class StillingFeedKlient(
    private val stillingFeedBaseUrl: String,
    private val stillingFeedToken: String,
    private val httpClient: HttpClient,
    private val objectMapper: ObjectMapper,
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
            HttpStatus.OK.code -> {
                log.info("Konsument opprettet: ${response.body()}")
                return response.body()
            }
            else -> {
                log.warn("Uventet respons ved opprettelse av konsument: ${response.statusCode()} - ${response.body()}")
                throw RuntimeException("Uventet respons ved opprettelse av konsument: ${response.statusCode()} - ${response.body()}")
            }
        }
    }

    fun hentKonsumenter(spørring: String): List<KonsumentDTO> {
        val response = sendGetRequest("/internal/api/consumers", mapOf("q" to spørring))
        val konsumenter: List<KonsumentDTO> = objectMapper.readValue(response.body())
        when (response.statusCode()) {
            HttpStatus.OK.code -> {
                log.info("Hentet ${konsumenter.size} konsumenter")
                return konsumenter
            }
            else -> {
                log.warn("Uventet respons ved henting av konsumenter: ${response.statusCode()} - ${response.body()}")
                throw RuntimeException("Uventet respons ved henting av konsumenter: ${response.statusCode()} - ${response.body()}")
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
            HttpStatus.OK.code -> {
                log.info("Token generert for konsument: ${tokenRequest.konsumentId}")
                return response.body()
            }
            else -> {
                log.warn("Uventet respons ved generering av token: ${response.statusCode()} - ${response.body()}")
                throw RuntimeException("Uventet respons ved generering av token: ${response.statusCode()} - ${response.body()}")
            }
        }
    }

    private fun Map<String, String>.toQueryString(): String =
        this.entries.joinToString("&") { "${it.key}=${URLEncoder.encode(it.value, "UTF-8")}" }

    private fun sendGetRequest(endepunkt: String, queryParameters: Map<String, String> = emptyMap(), token: String = stillingFeedToken) = httpClient.send(
        HttpRequest.newBuilder()
            .uri(URI("$stillingFeedBaseUrl$endepunkt${if (queryParameters.isNotEmpty()) "?${queryParameters.toQueryString()}" else ""}"))
            .GET()
            .setHeader("Authorization", "Bearer $token")
            .build(),
        BodyHandlers.ofString()
    )

    private fun sendPostRequest(endepunkt: String, body: String, token: String = stillingFeedToken) = httpClient.send(
        HttpRequest.newBuilder()
            .uri(URI("$stillingFeedBaseUrl$endepunkt"))
            .POST(BodyPublishers.ofString(body))
            .setHeader("Authorization", "Bearer $token")
            .build(),
        BodyHandlers.ofString()
    )
}
