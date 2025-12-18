package no.nav.pam.stilling.feed.admin

import kotlinx.html.*
import kotlinx.html.stream.createHTML
import no.nav.pam.stilling.feed.admin.komponenter.Navbar

fun templateHTML(
    title: String = "Stillingsfeed Admin",
    content: BODY.() -> Unit
): String = createHTML().html {
    head {
        meta { charset = "UTF-8" }
        link {
            rel = "icon"
            href = "/public/favicon.png"
        }
        link {
            rel = "stylesheet"
            href = "https://cdn.nav.no/aksel/@navikt/ds-css/7.20.0/index.min.css"
        }
        link {
            rel = "stylesheet"
            href = "/public/style.css"
        }
        script {
            src = "/public/libs/htmx.org@2.0.8.min.js"
        }
        script {
            src = "/public/libs/hyperscript.org@0.9.14.min.js"
        }
        title { +title }
    }
    body {
        content()
    }
}

fun indexHTML(
    content: FlowContent.() -> Unit = { }
): String = templateHTML {
    Navbar()
    main {
        id = "root"
        content()
    }
}
