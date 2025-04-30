package app

import no.nav.pam.stilling.feed.admin.app.env
import no.nav.pam.stilling.feed.admin.startApp

fun main() {
    val localAppContext = LocalApplicationContext(env)
    localAppContext.startApp()
}
