package no.nav.pam.stilling.feed.admin

import io.javalin.Javalin
import io.javalin.http.Context
import io.javalin.json.JavalinJackson
import io.javalin.micrometer.MicrometerPlugin
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import net.logstash.logback.argument.StructuredArguments.kv
import no.nav.pam.stilling.feed.admin.sikkerhet.JavalinAccessManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.util.*

fun main() {
    val env = System.getenv()
    val appContext = ApplicationContext(env)
    appContext.startApp()
}

fun ApplicationContext.startApp(): Javalin {
    val accessManager = JavalinAccessManager()

    val javalin = startJavalin(
        port = 3000,
        jsonMapper = JavalinJackson(objectMapper),
        meterRegistry = prometheusRegistry,
        accessManager = accessManager,
    )

    setupAllRoutes(javalin)

    return javalin
}

private fun ApplicationContext.setupAllRoutes(javalin: Javalin) {
//    naisController.setupRoutes(javalin)
    rootRouter.setupRoutes(javalin)
    konsumentRouter.setupRoutes(javalin)
    tokenRouter.setupRoutes(javalin)
}

fun startJavalin(
    port: Int,
    jsonMapper: JavalinJackson,
    meterRegistry: PrometheusMeterRegistry,
    accessManager: JavalinAccessManager,
): Javalin {
    val requestLogger = LoggerFactory.getLogger("access")
    val log = LoggerFactory.getLogger("no.nav.pam.stilling.feed.admin")
    val micrometerPlugin = MicrometerPlugin { micrometerConfig ->
        micrometerConfig.registry = meterRegistry
    }

    return Javalin.create { config ->
        config.router.ignoreTrailingSlashes = true
        config.router.treatMultipleSlashesAsSingleSlash = true
        config.requestLogger.http { ctx, ms ->
            if (!(ctx.path().endsWith("/internal/isReady") || ctx.path().endsWith("/internal/isAlive") || ctx.path()
                    .endsWith("/internal/prometheus"))
            ) logRequest(ctx, ms, requestLogger)
        }
        config.http.defaultContentType = "application/json"
        config.jsonMapper(jsonMapper)
        config.registerPlugin(micrometerPlugin)
    }.beforeMatched { ctx ->
        if (ctx.routeRoles().isEmpty()) {
            return@beforeMatched
        }
        accessManager.manage(ctx, ctx.routeRoles())
    }.before { ctx ->
        val callId = ctx.header("Nav-Call-Id") ?: ctx.header("Nav-CallId") ?: UUID.randomUUID().toString()
        ctx.attribute("TraceId", callId)
        MDC.put("TraceId", callId)
    }.after {
        MDC.remove("TraceId")
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
