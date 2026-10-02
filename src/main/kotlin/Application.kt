package no.nav.pam.stilling.feed.admin

import io.javalin.Javalin
import io.javalin.config.JavalinConfig
import io.javalin.http.Context
import io.javalin.http.HttpStatus
import io.javalin.json.JavalinJackson
import io.javalin.micrometer.MicrometerPlugin
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import io.opentelemetry.instrumentation.api.semconv.http.HttpServerRoute
import io.opentelemetry.instrumentation.api.semconv.http.HttpServerRouteSource
import net.logstash.logback.argument.StructuredArguments.kv
import no.nav.pam.stilling.feed.admin.sikkerhet.ForbiddenException
import no.nav.pam.stilling.feed.admin.sikkerhet.JavalinAccessManager
import no.nav.pam.stilling.feed.admin.sikkerhet.UnauthorizedException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.util.*

val log = LoggerFactory.getLogger("no.nav.pam.stilling.feed.admin")

fun main() {
    try {
        val env = System.getenv()
        val appContext = ApplicationContext(env)
        appContext.startApp()
    } catch (e: Exception) {
        log.error("Uventet feil ved oppstart av applikasjonen: ${e.message}", e)
    }
}

fun ApplicationContext.startApp(): Javalin {
    val accessManager = JavalinAccessManager()

    return startJavalin(
        port = 3000,
        jsonMapper = JavalinJackson(objectMapper),
        meterRegistry = prometheusRegistry,
        accessManager = accessManager,
        setupRoutes = { config -> setupAllRoutes(config) },
    )
}

private fun ApplicationContext.setupAllRoutes(config: JavalinConfig) {
    naisController.setupRoutes(config)
    rootRouter.setupRoutes(config)
    konsumentRouter.setupRoutes(config)
    tokenRouter.setupRoutes(config)
}

fun startJavalin(
    port: Int,
    jsonMapper: JavalinJackson,
    meterRegistry: PrometheusMeterRegistry,
    accessManager: JavalinAccessManager,
    setupRoutes: (JavalinConfig) -> Unit,
): Javalin {
    val requestLogger = LoggerFactory.getLogger("access")
    val micrometerPlugin = MicrometerPlugin { micrometerConfig ->
        micrometerConfig.registry = meterRegistry
    }

    return Javalin.create { config ->
        config.router.ignoreTrailingSlashes = true
        config.router.treatMultipleSlashesAsSingleSlash = true
        config.requestLogger.http { ctx, ms ->
            if (!(ctx.path().endsWith("/internal/isReady")
                        || ctx.path().endsWith("/internal/isAlive")
                        || ctx.path().endsWith("/internal/prometheus")
                        || ctx.path().contains("/public/")
                        )
            ) logRequest(ctx, ms, requestLogger)
        }
        config.http.defaultContentType = "application/json"
        config.jsonMapper(jsonMapper)
        config.registerPlugin(micrometerPlugin)
        config.staticFiles.add({ staticFiles ->
            staticFiles.directory = "/public"
            staticFiles.hostedPath = "/public"
        })

        setupRoutes(config)

        config.routes.before { ctx ->
            val callId = ctx.header("Nav-Call-Id") ?: ctx.header("Nav-CallId") ?: UUID.randomUUID().toString()
            ctx.attribute("TraceId", callId)
            MDC.put("TraceId", callId)
        }
        config.routes.beforeMatched { ctx ->
            ctx.endpoints().matchedHttpEndpoint()?.let { endepunkt ->
                HttpServerRoute.update(
                    io.opentelemetry.context.Context.current(),
                    HttpServerRouteSource.NESTED_CONTROLLER,
                    endepunkt.path
                )
            }
            val roles = ctx.routeRoles()
            if (roles.isNotEmpty()) {
                accessManager.manage(ctx, roles)
            }
        }
        config.routes.after {
            MDC.remove("TraceId")
        }

        config.routes.exception(ClassNotFoundException::class.java) { e, ctx ->
            log.warn("NotFoundException: ${e.message}", e)
            ctx.status(HttpStatus.NOT_FOUND).result(e.message ?: "")
        }
        config.routes.exception(ForbiddenException::class.java) { e, ctx ->
            log.warn("ForbiddenException: ${e.message}", e)
            ctx.status(HttpStatus.FORBIDDEN).result(e.message ?: "")
        }
        config.routes.exception(UnauthorizedException::class.java) { e, ctx ->
            log.warn("UnauthorizedException: ${e.message}", e)
            ctx.status(HttpStatus.UNAUTHORIZED).result(e.message ?: "")
        }
        config.routes.exception(IllegalArgumentException::class.java) { e, ctx ->
            log.warn("IllegalArgumentException: ${e.message}", e)
            ctx.status(HttpStatus.BAD_REQUEST).result(e.message ?: "")
        }
        config.routes.exception(Exception::class.java) { e, ctx ->
            log.error("Exception: ${e.message}", e)
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result(e.message ?: "")
        }
    }.start(port)
}

fun logRequest(ctx: Context, ms: Float, log: Logger) {
    log.info(
        "${ctx.method()} ${ctx.url()} ${ctx.statusCode()}",
        kv("method", ctx.method()),
        kv("requested_uri", ctx.path()),
        kv("requested_url", ctx.url()),
        kv("protocol", ctx.protocol()),
        kv("status_code", ctx.statusCode()),
        kv("TraceId", "${ctx.attribute<String>("TraceId")}"),
        kv("elapsed_ms", "$ms")
    )
}
