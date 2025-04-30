package no.nav.pam.stilling.feed.admin.sikkerhet

import io.javalin.http.Context
import io.javalin.security.RouteRole

class JavalinAccessManager {

    fun manage(ctx: Context, routeRoles: Set<RouteRole>) {
        TODO("Logikk for tilgangsstyring")
    }
}

class ForbiddenException : RuntimeException {
    constructor(cause: Throwable) : super(cause)
    constructor(msg: String) : super(msg)
    constructor(msg: String, cause: Throwable) : super(msg, cause)
    constructor() : super()
}

class UnauthorizedException : RuntimeException {
    constructor(cause: Throwable) : super(cause)
    constructor(msg: String) : super(msg)
    constructor(msg: String, cause: Throwable) : super(msg, cause)
    constructor() : super()
}

class NotFoundException : RuntimeException {
    constructor(cause: Throwable) : super(cause)
    constructor(msg: String) : super(msg)
    constructor(msg: String, cause: Throwable) : super(msg, cause)
    constructor() : super()
}
