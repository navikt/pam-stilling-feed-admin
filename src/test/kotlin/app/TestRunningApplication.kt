package app

import no.nav.pam.stilling.feed.admin.app.env
import no.nav.pam.stilling.feed.admin.startApp

abstract class TestRunningApplication {
    companion object {
        const val lokalBaseUrl = "http://localhost:3000"

        @JvmStatic
        val appCtx = TestApplicationContext(env)
        val javalin = appCtx.startApp()
    }
}
