package no.nav.pam.stilling.feed.admin.komponenter

import kotlinx.html.*
import java.net.URI

class EpostMalProps {
    lateinit var oneTimeSecretUrl: URI
    lateinit var passphrase: String
}

val tekstmal = """
    Hei,

    Her er lenken for dokumentasjonen for api-et: https://navikt.github.io/pam-stilling-feed/
    Der finner du også lenker til swagger/openapi og redoc spesifikasjoner.

    Tokenet er tilgjengelig her:
    <ONETIMESECRET_URL>

    Passphrasen er: <ONETIMESECRET_PASSPHRASE>
    Lenken er bare gyldig i 7 dager og kan bare åpnes en gang, så pass på at du får lagret tokenet et sted.
    Hvis du ikke fikk hentet tokenet innenfor tiden, eller du ikke fikk tak i det, så bare send meg en e-post så kan jeg sende deg et nytt token.

    Har du noen spørsmål, så er det bare å sende oss en e-post på nav.team.arbeidsplassen@nav.no.
""".trimIndent()

fun FlowContent.EpostMal(block: EpostMalProps.() -> Unit) {
    val props = EpostMalProps().apply(block)

    val epost = tekstmal
        .replace("<ONETIMESECRET_URL>", props.oneTimeSecretUrl.toString())
        .replace("<ONETIMESECRET_PASSPHRASE>", props.passphrase)

    textArea {
        id = "epostmal"
        classes = setOf("navds-textarea__input")
        rows = epost.count { it == '\n' }.plus(4).toString() // sett rader lik antall linjer + 4 for padding
        +epost
    }

    Button {
        val standardLabel = "Kopier e-postmal"
        val kopiertLabel = "\u2713 Kopiert!"
        id = "kopierEpostmal"
        type = ButtonType.button
        variant = ButtonVariant.PRIMARY
        attributes["_"] = "on click queue last set :epost to #epostmal.textContent " +
                          "then call the window's navigator's clipboard's writeText(:epost) " +
                          "then set my.firstChild.textContent to '$kopiertLabel' " +
                          "then wait 1 seconds then set my.firstChild.textContent to '$standardLabel'"
        label = standardLabel
    }
}
