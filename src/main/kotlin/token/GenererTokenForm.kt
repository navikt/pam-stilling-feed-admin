package no.nav.pam.stilling.feed.admin.token

import kotlinx.html.*

fun FlowContent.GenererTokenForm() {
    form {
        id = "genererTokenForm"
        attributes["hx-post"] = "/token/generer"
        attributes["hx-target"] = "#genererTokenForm"
        attributes["hx-swap"] = "outerHTML"

        label {
            htmlFor = "konsumentId"
            +"Konsument ID:"
        }
        input {
            type = InputType.text
            name = "konsumentId"
            id = "konsumentId"
            required = true
            autoFocus = true
        }

        label {
            htmlFor = "expires"
            +"Utløpsdato: (La være tom for ingen utløpsdato)"
        }
        input {
            type = InputType.dateTimeLocal
            name = "expires"
            required = false
            id = "expires"
        }

        button {
            type = ButtonType.submit
            +"Generer token"
        }
    }
}
