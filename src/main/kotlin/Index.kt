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
            href = "/favicon.png"
        }
        link {
            rel = "stylesheet"
            href = "https://cdn.nav.no/aksel/@navikt/ds-css/7.20.0/index.min.css"
        }
        link {
            rel = "stylesheet"
            href = "/style.css"
        }
        script {
            src = "https://unpkg.com/htmx.org@2.0.4"
            integrity = "sha384-HGfztofotfshcF7+8n44JQL2oJmowVChPTg48S+jvZoztPfvwD79OC/LTtG6dMp+"
            crossorigin = ScriptCrossorigin.anonymous
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
    div { id = "error" }
    main {
        id = "root"
        content()
    }
}
