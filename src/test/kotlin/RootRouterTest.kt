import app.TestRunningApplication
import io.javalin.http.ContentType
import io.javalin.http.HttpStatus
import it.skrape.core.htmlDocument
import it.skrape.fetcher.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RootRouterTest : TestRunningApplication() {
    @Test
    fun `Skal redirecte på root path`() {
        skrape(HttpFetcher) {
            request {
                url = "$lokalBaseUrl/"
                method = Method.GET
                followRedirects = false
            }
            response {
                status { assertThat(code).isEqualTo(HttpStatus.FOUND.code) }
            }
        }
    }

    @Test
    fun `Skal laste siden for 'finn konsument'`() {
        skrape(HttpFetcher) {
            request {
                url = "$lokalBaseUrl/konsument"
                method = Method.GET
            }
            response {
                status { assertThat(code).isEqualTo(HttpStatus.OK.code) }
                assertThat(contentType).contains(ContentType.TEXT_HTML.mimeType)
                htmlDocument {
                    findFirst("#konsumentSok") {
                        assertThat(tagName).isEqualTo("div")
                        assertThat(attributes["hx-get"]).isEqualTo("/konsument/sok")
                        assertThat(attributes["hx-target"]).isEqualTo("#konsumentSok")
                        assertThat(attributes["hx-trigger"]).isEqualTo("load")
                    }
                }
            }
        }
    }

    @Test
    fun `Skal laste siden for 'opprett konsument'`() {
        skrape(HttpFetcher) {
            request {
                url = "$lokalBaseUrl/konsument/opprett"
                method = Method.GET
            }
            response {
                status { assertThat(code).isEqualTo(HttpStatus.OK.code) }
                htmlDocument {
                    findFirst("#konsument") {
                        assertThat(tagName).isEqualTo("section")
                        assertThat(attributes["hx-get"]).isEqualTo("/konsument/form")
                        assertThat(attributes["hx-target"]).isEqualTo("#konsument")
                        assertThat(attributes["hx-trigger"]).isEqualTo("load")
                    }
                }
            }
        }
    }

    @Test
    fun `Skal laste siden for 'generer token'`() {
        skrape(HttpFetcher) {
            request {
                url = "$lokalBaseUrl/token/generer"
                method = Method.GET
            }
            response {
                status { assertThat(code).isEqualTo(HttpStatus.OK.code) }
                htmlDocument {
                    findFirst("#genererToken") {
                        assertThat(tagName).isEqualTo("section")
                        assertThat(attributes["hx-get"]).isEqualTo("/token/form")
                        assertThat(attributes["hx-target"]).isEqualTo("#genererToken")
                        assertThat(attributes["hx-trigger"]).isEqualTo("load")
                    }
                }
            }
        }
    }
}
