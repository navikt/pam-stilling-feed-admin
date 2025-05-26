package app

import no.nav.pam.stilling.feed.admin.app.env
import no.nav.pam.stilling.feed.admin.startApp

fun main() {
    val testAppCtx = TestApplicationContext(env)
    testAppCtx.startApp()
}
