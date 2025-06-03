package no.nav.pam.stilling.feed.admin.komponenter

import kotlinx.html.*

fun FlowContent.Laster() {
    div {
        id = "laster"
        classes = setOf("navds-loader")
        style = """
                  |display: none;
                  |position: fixed;
                  |top: 50%;
                  |left: 0;
                  |z-index: 10000;
                  |width: 100%;
                  |font-size: 2rem;
                  |text-align: center;
                  |""".trimMargin()
        +"Vær tålmodig, krønsjer noen tall..."
    }
}
