package no.nav.pam.stilling.feed.admin.nais

import io.javalin.config.JavalinConfig
import io.javalin.http.ContentType
import io.javalin.http.HttpStatus
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry


class NaisController(
    private val healthService: HealthService, private val prometheusMeterRegistry: PrometheusMeterRegistry
) {
    fun setupRoutes(config: JavalinConfig) {
        config.routes.get("/internal/isReady") { it.status(200) }
        config.routes.get("/internal/isAlive") {
            if (healthService.isHealthy()) it.status(HttpStatus.OK)
            else it.status(HttpStatus.SERVICE_UNAVAILABLE)
        }
        config.routes.get("/internal/prometheus") {
            it.contentType(ContentType.TEXT_PLAIN).result(prometheusMeterRegistry.scrape())
        }
    }
}
