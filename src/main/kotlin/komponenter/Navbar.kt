package no.nav.pam.stilling.feed.admin.komponenter

import kotlinx.html.*

val sider = listOf(
    "Opprett konsument" to "/konsument/opprett",
    "Generer token" to "/token/generer",
    "Finn konsument" to "/konsument",
)

fun FlowContent.Navbar(
    tittel: String = "Stillingsfeed Admin",
    lenker: List<Pair<String, String>> = sider,
) {
    nav {
        h1 {
            a {
                id = "tittel"
                href = "/"
                +tittel
            }
        }
        div {
            lenker.map { lenke ->
                a {
                    href = lenke.second
                    +lenke.first
                }
            }
        }
    }
}
