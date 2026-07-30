package com.mechanica.engine.compose

import com.dubulduke.layout.DukeBackend
import com.dubulduke.layout.Element
import com.dubulduke.layout.NodeView
import com.dubulduke.layout.fontOf
import com.dubulduke.layout.widthOf
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.shaders.text.Text
import java.util.IdentityHashMap

/**
 * Draws DukeCompose elements with a Mechanica [Drawer]. `dsl-and-engine.md` §6.1.
 *
 * The renderer arrives as a parameter to every call rather than living in a field, which is what
 * deletes iteration C's `private var drawer: Drawer?` and its `require(draw != null)` on every
 * element.
 *
 * Geometry is already in window coordinates by the time it gets here — the viewport transform
 * lives in DukeCompose's draw walk — so this never sees layout coordinates. Extents arrive
 * *signed*: Mechanica's UI camera is y-up while layout is y-down, so `height` is normally
 * negative. `Drawer.rectangle` handles that, and the text placement below is iteration A's
 * formula unchanged for the same reason.
 */
open class MechanicaBackend<S>(
    /**
     * **The same instance the [ElementTree][com.dubulduke.layout.ElementTree] measures with.**
     * Layout decides how tall the text is by wrapping it; this has to draw the identical breaks,
     * or the box and its contents disagree. Two metrics objects with the same settings would
     * agree, but nothing would keep them that way.
     */
    private val metrics: MechanicaTextMetrics = MechanicaTextMetrics(),
) : DukeBackend<S, Drawer> where S : HasBackground, S : HasText {

    /**
     * One [Text] per text element, plus what it was last built from.
     *
     * `Text` rebuilds its vertex buffers in its constructor and again on every `string` / `font`
     * assignment, so allocating or reassigning one per frame would re-tesselate every glyph in the
     * UI every frame. Caching the inputs means a static label costs nothing after its first frame,
     * and a re-wrap happens exactly when the text, the font or the laid-out width changed.
     *
     * Keyed by element identity, which is stable because composition happens once; **this leaks
     * one entry per text element retired by a recomposition**, and wants clearing from
     * reconciliation when that exists.
     */
    private class Cached(@JvmField val model: Text) {
        @JvmField var source: String? = null
        @JvmField var width: Double = Double.NaN
        @JvmField var font: MechanicaFont? = null
    }

    private val texts = IdentityHashMap<Element, Cached>()

    override fun drawBox(node: NodeView<S>, renderer: Drawer) {
        val style = node.styleOrNull ?: return
        fill(node, style, renderer)
    }

    override fun drawText(node: NodeView<S>, text: String, renderer: Drawer) {
        val style = node.styleOrNull ?: return
        if (!style.isVisible) return
        fill(node, style, renderer)

        // The font layout measured with, so that what is drawn matches what was laid out. Any
        // other FontRef is one DukeCompose could measure but Mechanica cannot draw, which is
        // silent misalignment rather than an error — worth failing loudly for.
        val content = node.element.content ?: return
        val font = node.graph.fontOf(content) as? MechanicaFont
            ?: throw IllegalStateException(
                "${node.element} was laid out with a ${node.graph.fontOf(content)::class.simpleName}, " +
                        "which MechanicaBackend cannot draw; use a MechanicaFont"
            )

        // Mechanica's `Text` breaks on explicit \n and nothing else, so the wrap layout used to
        // decide this element's height has to be materialised into the string. Without this the
        // box is three lines tall and the text is one long line running out the side of it.
        //
        // Measured in layout units, not `node.width`: the latter has been through the viewport
        // transform and is signed, while the metrics wrapped in the same space layout works in.
        val width = node.graph.widthOf(node.element)

        val cached = texts.getOrPut(node.element) { Cached(Text(text, font.font)) }
        if (cached.source != text || cached.width != width || cached.font !== font) {
            val model = cached.model
            // Both setters re-tesselate, so only assign on an actual change. This is the
            // draw-side twin of RefEquality.EQUALS on the text node.
            if (model.font !== font.font) model.font = font.font
            val broken = metrics.wrapped(text, font, width)
            if (model.string != broken) model.string = broken
            cached.source = text
            cached.width = width
            cached.font = font
        }

        val a = style.textAlignment
        renderer.ui.color(style.textColor)
            .origin.normalized(a.x, 1.0 - a.y)
            .text(cached.model, font.size, node.x + a.x * node.width, node.y + a.y * node.height)
    }

    /**
     * Paint this element's background.
     *
     * `protected open` on purpose: this is the seam for a custom-shaded widget. A subclass with a
     * richer style type can override it, check its own capability — a glow level, a shader handle —
     * and fall back to `super.fill(...)` for the ordinary case, without reimplementing text
     * handling or the wrap cache. Overriding [drawCustom] and routing through `kind("…")` is the
     * other route, and the better one when the widget is not a rectangle at all.
     */
    protected open fun fill(node: NodeView<S>, style: S, renderer: Drawer) {
        if (!style.isVisible || style.color.a <= 0.0) return

        renderer.ui
            .radius(style.radius)
            .color(style.color)
            .rectangle(node.x, node.y, node.width, node.height)
    }
}
