package no.nav.pam.stilling.feed.admin

import io.javalin.Javalin
import io.javalin.http.Context
import io.javalin.http.HttpStatus
import io.javalin.json.JavalinJackson
import io.javalin.micrometer.MicrometerPlugin
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
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
    naisController.setupRoutes(javalin)
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
    }.exception(ClassNotFoundException::class.java) { e, ctx ->
        log.warn("NotFoundException: ${e.message}", e)
        ctx.status(HttpStatus.NOT_FOUND).result(e.message ?: "")
    }.exception(ForbiddenException::class.java) { e, ctx ->
        log.warn("ForbiddenException: ${e.message}", e)
        ctx.status(HttpStatus.FORBIDDEN).result(e.message ?: "")
    }.exception(UnauthorizedException::class.java) { e, ctx ->
        log.warn("UnauthorizedException: ${e.message}", e)
        ctx.status(HttpStatus.UNAUTHORIZED).result(e.message ?: "")
    }.exception(IllegalArgumentException::class.java) { e, ctx ->
        log.warn("IllegalArgumentException: ${e.message}", e)
        ctx.status(HttpStatus.BAD_REQUEST).result(e.message ?: "")
    }.exception(Exception::class.java) { e, ctx ->
        log.error("Exception: ${e.message}", e)
        ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result(e.message ?: "")
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
