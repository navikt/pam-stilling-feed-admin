package app

import no.nav.pam.stilling.feed.admin.ApplicationContext

class LocalApplicationContext(
    private val localEnv: Map<String, String>
): ApplicationContext(localEnv)
