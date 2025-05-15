package no.nav.pam.stilling.feed.admin.app

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.time.Instant
import java.util.*

private val lokalAdminToken = JWT.create()
    .withSubject("admin@arbeidsplassen.nav.no")
    .withClaim("kid", UUID.randomUUID().toString())
    .withIssuer("nav-test")
    .withAudience("feed-api-v2-test")
    .withIssuedAt(Instant.now())
    .withExpiresAt(null as Instant?)
    .sign(Algorithm.HMAC256("SuperHemmeligNøkkel"))

val env = mutableMapOf(
    "STILLING_FEED_BASE_URL" to "http://localhost:8080",
    "STILLING_FEED_ADMIN_TOKEN" to lokalAdminToken,
    "ONETIMESECRET_PASSPHRASE" to "arbeidsplassen",
    "ONETIMESECRET_BASE_URL" to "https://eu.onetimesecret.com"
)
