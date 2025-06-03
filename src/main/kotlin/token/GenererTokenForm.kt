package no.nav.pam.stilling.feed.admin.token

import kotlinx.html.*
import no.nav.pam.stilling.feed.admin.komponenter.*
import no.nav.pam.stilling.feed.admin.konsument.KonsumentDTO

fun FlowContent.GenererTokenForm(
    konsumenter: List<KonsumentDTO>
) {
    form {
        attributes["hx-post"] = "/token/generer"
        attributes["hx-target"] = "#genererToken"
        attributes["hx-trigger"] = "submit"
        attributes["hx-swap"] = "outerHTML"
        attributes["hx-indicator"] = "#laster"

        ModalContent() // modal content setter globale variabler for heading og body så den må lastes først

        div {
            style = "display: flex; flex-direction: column;"
            label {
                classes = setOf("navds-label")
                htmlFor = "konsumentId"
                +"Velg konsument:"
            }

            select {
                name = "konsumentId"
                classes = setOf("navds-select__input")
                attributes["_"] = "on load or change log my.selectedOptions.innerHTML[0] then set global heading.value to " +
                        "`Generer token for konsument: \${my.selectedOptions.innerHTML[0]}`" +
                        "then set global body.value to " +
                        "`Er du sikker på at du vil generere token for kosumenten: \${my.selectedOptions.innerHTML[0]}? Denne handlingen kan ikke angres.`"
                konsumenter.forEach { konsument ->
                    option {
                        value = konsument.id.toString()
                        +konsument.identifikator
                    }
                }
            }
        }

        Input {
            name = "expires"
            label = "Utløpsdato: (La være tom for ingen utløpsdato)"
            type = InputType.dateTimeLocal
            required = false
        }

        Button {
            type = ButtonType.button
            variant = ButtonVariant.PRIMARY
            attributes["hx-get"] = "/modal"
            attributes["hx-target"] = "#modalContent"
            attributes["hx-swap"] = "beforeend"
            attributes["hx-include"] = "#modalContent"
            attributes["hx-indicator"] = "#modalContent"
            label = "Generer token"
        }
    }
}
