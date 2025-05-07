package no.nav.pam.stilling.feed.admin.token

import kotlinx.html.*
import no.nav.pam.stilling.feed.admin.komponenter.*

fun FlowContent.GenererTokenForm() {
    form {
        id = "genererTokenForm"
        attributes["hx-post"] = "/token/generer"
        attributes["hx-target"] = "#genererTokenForm"
        attributes["hx-swap"] = "outerHTML"

        Input {
            name = "konsumentId"
            label = "Konsument ID:"
            required = true
            autoFocus = true
        }

        Input {
            name = "expires"
            label = "Utløpsdato: (La være tom for ingen utløpsdato)"
            type = InputType.dateTimeLocal
            required = false
        }

        Button {
            type = ButtonType.submit
            variant = ButtonVariant.PRIMARY
            label = "Generer token"
        }
    }
}
