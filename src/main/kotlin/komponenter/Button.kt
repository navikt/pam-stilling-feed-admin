package no.nav.pam.stilling.feed.admin.komponenter

import kotlinx.html.*
import java.util.*

enum class ButtonVariant(val variant: String) {
    PRIMARY("primary"),
    SECONDARY("secondary"),
    TERTIARY("tertiary"),
}

enum class ButtonSize(val size: String) {
    XSMALL("xsmall"),
    SMALL("small"),
    MEDIUM("medium"),
}

class ButtonProps {
    var id: String? = null
    var type: ButtonType = ButtonType.button
    var variant: ButtonVariant = ButtonVariant.PRIMARY
    var size: ButtonSize = ButtonSize.MEDIUM
    var label: String? = null
    var attributes: MutableMap<String, String> = mutableMapOf()
}

fun FlowOrInteractiveContent.Button(block: ButtonProps.() -> Unit) {
    val props = ButtonProps().apply(block)
    button {
        props.id?.let { id = it }
        classes = setOf("navds-button", "navds-button--${props.variant}", "navds-button--${props.size.size}")
        type = props.type
        props.attributes.forEach { (key, value) ->
            attributes[key] = value
        }

        label {
            classes = setOf("navds-label")
            props.label?.let { +it } ?: +props.type.name.replaceFirstChar { it.uppercase() }
        }
    }
}
