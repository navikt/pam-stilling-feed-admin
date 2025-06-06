package no.nav.pam.stilling.feed.admin.nais

import java.util.concurrent.atomic.AtomicInteger

class HealthService {
    private val unhealthyVotes = AtomicInteger(0)
    fun addUnhealthyVote() = unhealthyVotes.addAndGet(1)
    fun isHealthy() = unhealthyVotes.get() == 0
}
