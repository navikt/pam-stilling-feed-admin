package no.nav.pam.stilling.feed.admin.token

import kotlinx.html.*
import no.nav.pam.stilling.feed.admin.komponenter.*
import no.nav.pam.stilling.feed.admin.konsument.KonsumentDTO

fun FlowContent.GenererTokenForm(
    konsumenter: List<KonsumentDTO>
) {
    form {
        attributes["hx-post"] = "/token/generer"
        attributes["hx-target"] = "#genererTokenForm"
        attributes["hx-swap"] = "outerHTML"
        attributes["hx-indicator"] = "#laster"

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
            type = ButtonType.submit
            variant = ButtonVariant.PRIMARY
            label = "Generer token"
        }

        div {
            id = "laster"
            classes = setOf("navds-loader")
            style = "display: none; width: 100%; font-size: 2rem; text-align: center;"
            +"Vær tålmodig, krønsjer noen tall..."
        }
    }
}
