package no.nav.pam.stilling.feed.admin

import kotlinx.html.*
import kotlinx.html.consumers.delayed
import kotlinx.html.consumers.onFinalizeMap
import kotlinx.html.stream.HTMLStreamBuilder
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class FRAGMENT(
    initialAttributes: Map<String, String>,
    override val consumer: TagConsumer<*>,
) : HTMLTag("fragment", consumer, initialAttributes, null, false, false), HtmlBlockTag

/**
 * En tag som kan brukes til å sende flere elementer uten en wrapper via createHTML
 * Inspirert av Fragment komponenten i React: [https://react.dev/reference/react/Fragment]
 */
@HtmlTagMarker
@OptIn(ExperimentalContracts::class)
inline fun <T, C : TagConsumer<T>> C.fragment(
    classes: String? = null,
    crossinline block: FRAGMENT.() -> Unit = {}
): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return FRAGMENT(attributesMapOf("class", classes), this).visitAndFinalize(this, block)
}

/**
 * Identisk med createHTML bortsett fra at den fjerner fragmet tagger
 */
fun createFragmentHTML(prettyPrint: Boolean = true, xhtmlCompatible: Boolean = false): TagConsumer<String> =
    HTMLStreamBuilder(
        StringBuilder(),
        prettyPrint,
        xhtmlCompatible
    ).onFinalizeMap { sb, _ -> sb.toString().replace(Regex("<fragment>|</fragment>"), "") }.delayed()
