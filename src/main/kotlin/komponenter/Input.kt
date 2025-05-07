package no.nav.pam.stilling.feed.admin.komponenter

import kotlinx.html.*

class InputProps {
    var id: String? = null
    var name: String = ""
    var label: String = ""
    var type: InputType = InputType.text
    var placeholder: String? = null
    var value: String? = null
    var required: Boolean = false
    var autoFocus: Boolean = false
    var attributes: MutableMap<String, String> = mutableMapOf()
}

fun FlowContent.Input(block: InputProps.() -> Unit) {
    val props = InputProps().apply(block)

    div {
        style = """
            width: inherit;
            display: flex;
            flex-direction: column;
            ${if (props.label.isEmpty()) "align-items: center;" else "align-items: flex-start;"}
        """.trimIndent()

        if (props.label.isNotEmpty()) {
            label {
                classes = setOf("navds-label")
                htmlFor = props.name
                +props.label
            }
        }

        input {
            id = props.id ?: props.name
            classes = setOf("navds-text-field__input")
            name = props.name
            type = props.type
            required = props.required
            autoFocus = props.autoFocus
            props.placeholder?.let { placeholder = it }
            props.value?.let { value = it }
            props.attributes.forEach { (key, value) ->
                attributes[key] = value
            }
        }

    }
}

