package no.nav.pam.stilling.feed.admin.komponenter

import kotlinx.html.*

class ModalProps {
    var id: String = "modal"
    var heading: String = "Modal"
    var body: String = "Brødtekst, lofftekst, og baguettetekst"
}

fun FlowContent.ModalContent() {
    span {
        id = "modalContent"
        input(type = InputType.hidden, name = "heading") { attributes["_"] = "on load set global heading to me" }
        input(type = InputType.hidden, name = "body") { attributes["_"] = "on load set global body to me" }
    }
}

fun FlowContent.Modal(block: ModalProps.() -> Unit = {}) {
    val props = ModalProps().apply(block)
    dialog {
        id = props.id
        attributes["open"] = "true"
        attributes["_"] = "on closeModal add .closing then wait for animationend then remove me"

        div {
            classes = setOf("modal-underlay")
            attributes["_"] = "on click trigger closeModal"
        }

        div {
            classes = setOf("modal-content")
            h2 { +props.heading }
            p { +props.body }

            div {
                classes = setOf("modal-buttons")
                Button {
                    type = ButtonType.submit
                    attributes["_"] = "on click trigger closeModal"
                    label = "Bekreft"
                }

                Button {
                    variant = ButtonVariant.SECONDARY
                    attributes["_"] = "on click trigger closeModal"
                    label = "Avbryt"
                }
            }
        }
    }
}
