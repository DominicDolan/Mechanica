package svg

import org.jsoup.nodes.Element

open class SVGElement(private val element: Element) {
    val color: Long = parseColor(element.attr("fill"), element.attr("opacity"))

    operator fun get(attribute: String) {
        element.attr(attribute)
    }
}