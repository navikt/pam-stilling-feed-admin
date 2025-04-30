package no.nav.pam.stilling.feed.admin.sikkerhet

import io.javalin.http.Context
import io.javalin.security.RouteRole

class JavalinAccessManager {

    fun manage(ctx: Context, routeRoles: Set<RouteRole>) {
        TODO("Logikk for tilgangsstyring")
    }
}
