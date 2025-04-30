package no.nav.pam.stilling.feed.admin

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics
import io.micrometer.core.instrument.binder.logging.LogbackMetrics
import io.micrometer.core.instrument.binder.system.ProcessorMetrics
import io.micrometer.core.instrument.binder.system.UptimeMetrics
import io.micrometer.prometheusmetrics.PrometheusConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import no.nav.pam.stilling.feed.admin.konsument.KonsumentRouter
import no.nav.pam.stilling.feed.admin.konsument.KonsumentService
import no.nav.pam.stilling.feed.admin.token.TokenRouter
import no.nav.pam.stilling.feed.admin.token.TokenService
import java.net.http.HttpClient
import java.util.*

open class ApplicationContext(env: Map<String, String>) {
    val objectMapper: ObjectMapper = jacksonObjectMapper().registerModule(JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .setTimeZone(TimeZone.getTimeZone("Europe/Oslo"))

    val prometheusRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT).also { registry ->
        ClassLoaderMetrics().bindTo(registry)
        JvmMemoryMetrics().bindTo(registry)
        JvmGcMetrics().bindTo(registry)
        JvmThreadMetrics().bindTo(registry)
        UptimeMetrics().bindTo(registry)
        ProcessorMetrics().bindTo(registry)
        LogbackMetrics().bindTo(registry)
    }

    val httpClient: HttpClient = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .followRedirects(HttpClient.Redirect.ALWAYS)
        .build()

    val stillingFeedKlient = StillingFeedKlient(
        stillingFeedBaseUrl = env.getValue("STILLING_FEED_BASE_URL"),
        stillingFeedToken = env.getValue("STILLING_FEED_ADMIN_TOKEN"),
        httpClient = httpClient,
    )

    val rootRouter = RootRouter()

    val konsumentService = KonsumentService(stillingFeedKlient, objectMapper)
    val konsumentRouter = KonsumentRouter(konsumentService)

    val tokenService = TokenService(stillingFeedKlient)
    val tokenRouter = TokenRouter(tokenService)
}
