package no.nav.pam.stilling.feed.admin.konsument

import kotlinx.html.*

fun FlowContent.KonsumentForm() {
    val formFelt = listOf(
        "identifikator" to "Identifikator",
        "email" to "Email",
        "telefon" to "Telefon",
        "kontaktperson" to "Kontaktperson"
    )

    form {
        id = "konsumentForm"
        attributes["hx-post"] = "/konsument/opprett"
        attributes["hx-target"] = "#konsumentForm"
        attributes["hx-swap"] = "outerHTML"

        formFelt.forEach { (name, label) ->
            label {
                htmlFor = name
                +"$label:"
            }
            input(type = InputType.text, name = name) {
                id = name
                required = true
            }
        }
        button {
            type = ButtonType.submit
            +"Legg til konsument"
        }
    }
}
