package no.nav.pam.stilling.feed.admin.konsument

import kotlinx.html.*
import no.nav.pam.stilling.feed.admin.komponenter.Button
import no.nav.pam.stilling.feed.admin.komponenter.Input

fun FlowContent.KonsumentForm() {
    form {
        id = "konsumentForm"
        attributes["hx-post"] = "/konsument/opprett"
        attributes["hx-target"] = "#konsument"
        attributes["hx-trigger"] = "submit"
        attributes["hx-indicator"] = "#laster"

        Input {
            name = "identifikator"
            label = "Identifikator:"
            type = InputType.text
            required = true
            autoFocus = true
            attributes["_"] = "on input set global heading.value to " +
                              "`Opprett konsument: \${my value}` " +
                              "then set global body.value to " +
                              "`Er du sikker på at du vil opprette og generere token for kosumenten: \${my value}?` "
        }

        Input {
            name = "email"
            label = "E-post:"
            type = InputType.email
            required = true
        }

        Input {
            name = "telefon"
            label = "Telefon:"
            type = InputType.tel
            required = true
        }

        Input {
            name = "kontaktperson"
            label = "Kontaktperson:"
            type = InputType.text
            required = true
        }

        Button {
            id = "modalButton"
            attributes["hx-get"] = "/modal"
            attributes["hx-target"] = "#konsumentForm"
            attributes["hx-swap"] = "beforeend"
            attributes["hx-include"] = "#modalContent"
            attributes["hx-indicator"] = "#modalContent"
            label = "Legg til konsument"
        }
        span {
            id = "modalContent"
            input(type = InputType.hidden, name = "heading") { attributes["_"] = "on load set global heading to me" }
            input(type = InputType.hidden, name = "body") { attributes["_"] = "on load set global body to me" }
        }
    }
}
