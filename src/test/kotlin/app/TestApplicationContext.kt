package app

import io.mockk.mockk
import no.nav.pam.stilling.feed.admin.ApplicationContext
import no.nav.pam.stilling.feed.admin.konsument.KonsumentRouter
import no.nav.pam.stilling.feed.admin.konsument.KonsumentService
import no.nav.pam.stilling.feed.admin.token.TokenRouter
import no.nav.pam.stilling.feed.admin.token.TokenService

class TestApplicationContext(
    private val testEnv: MutableMap<String, String>,
) : ApplicationContext(testEnv) {
    val konsumentServiceMock = mockk<KonsumentService>()
    override val konsumentRouter by lazy { KonsumentRouter(konsumentServiceMock, httpClient, lokalBaseUrl) }

    val tokenServiceMock = mockk<TokenService>()
    override val tokenRouter by lazy { TokenRouter(tokenServiceMock, konsumentServiceMock) }
}
