package no.nav.pam.stilling.feed.admin.konsument

import kotlinx.html.*
import no.nav.pam.stilling.feed.admin.komponenter.Button
import no.nav.pam.stilling.feed.admin.komponenter.Input

fun FlowContent.KonsumentForm() {
    form {
        attributes["hx-post"] = "/konsument/opprett"
        attributes["hx-target"] = "#konsument"

        Input {
            name = "identifikator"
            label = "Identifikator:"
            type = InputType.text
            required = true
            autoFocus = true
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
            type = ButtonType.submit
            label = "Legg til konsument"
        }
    }
}
