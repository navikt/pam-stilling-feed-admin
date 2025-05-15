package no.nav.pam.stilling.feed.admin.token

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.javalin.http.ContentType
import io.javalin.http.Header
import io.javalin.http.HttpStatus
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse.BodyHandlers

class OneTimeSecretKlient(
    val baseUrl: String,
    val passphrase: String,
    private val httpClient: HttpClient,
    private val objectMapper: ObjectMapper,
) {
    companion object {
        val log: Logger = LoggerFactory.getLogger(OneTimeSecretKlient::class.java)
    }

    private val baseApiUrl = "$baseUrl/api/v2"

    fun opprettSecret(melding: String): OneTimeSecretDTO {
        val requestBody = """
            {
                "secret": {
                    "secret": "${melding.trim()}",
                    "passphrase": "$passphrase",
                    "ttl": ${60 * 60 * 24 * 7}
                }
            }
        """.trimIndent()

        val response = sendPostRequest("/secret/conceal", requestBody)

        when (response.statusCode()) {
            HttpStatus.OK.code -> {
                val oneTimeSecret = objectMapper.readValue<OneTimeSecretDTO>(response.body())
                log.info("OneTimeSecret opprettet: ${oneTimeSecret.record?.metadata?.identifier}")
                return oneTimeSecret
            }
            else -> {
                log.error("Feil ved opprettelse av OneTimeSecret: ${response.statusCode()} - ${response.body()}")
                throw RuntimeException("Feil ved opprettelse av OneTimeSecret: ${response.statusCode()} - ${response.body()}")
            }
        }
    }

    fun hentMetadata(identifier: String): OneTimeSecretMetadataDTO {
        val response = sendGetRequest("/private/$identifier")

        return when (response.statusCode()) {
            HttpStatus.OK.code -> {
                log.info("Henter metadata for OneTimeSecret: $identifier")
                objectMapper.readValue<OneTimeSecretMetadataDTO>(response.body())
            }
            else -> {
                log.error("Feil ved henting av metadata for OneTimeSecret $identifier: ${response.statusCode()} - ${response.body()}")
                throw RuntimeException("Feil ved henting av metadata for OneTimeSecret $identifier: ${response.statusCode()} - ${response.body()}")
            }
        }
    }

    private fun sendGetRequest(endepunkt: String) = httpClient.send(
        HttpRequest.newBuilder().uri(URI("$baseApiUrl$endepunkt")).GET().build(), BodyHandlers.ofString()
    )

    private fun sendPostRequest(endepunkt: String, body: String) = httpClient.send(
        HttpRequest.newBuilder().uri(URI("$baseApiUrl$endepunkt"))
            .header(Header.CONTENT_TYPE, ContentType.APPLICATION_JSON.mimeType).POST(BodyPublishers.ofString(body))
            .build(), BodyHandlers.ofString()
    )
}
