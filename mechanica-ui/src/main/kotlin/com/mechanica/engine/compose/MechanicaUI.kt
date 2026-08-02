package com.mechanica.engine.compose

import com.dubulduke.dsl.DukeApp
import com.dubulduke.dsl.DukeInput
import com.dubulduke.dsl.StyleFactory
import com.dubulduke.layout.ClipRect
import com.dubulduke.layout.Element
import com.dubulduke.layout.NodeView
import com.dubulduke.layout.Viewport
import com.dubulduke.layout.Window
import com.dubulduke.layout.fontOf
import com.dubulduke.layout.widthOf
import com.mechanica.engine.context.loader.MechanicaFactory
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.game.view.UICamera
import com.mechanica.engine.input.mouse.Mouse
import com.mechanica.engine.shaders.text.Text
import java.util.IdentityHashMap
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor

/**
 * DukeCompose on a Mechanica UI camera: the whole integration in one class.
 *
 * ```kotlin
 * class MyScene : Scene() {
 *     private val ui = MechanicaUI()
 *
 *     init {
 *         ui.compose {
 *             layout {
 *                 left { 0.0 }; width { ui.viewportWidth }
 *                 top { 0.0 }; height { ui.viewportHeight }
 *             }
 *             …
 *         }
 *     }
 *
 *     override fun update(delta: Double) = ui.update()
 *     override fun render(draw: Drawer) = ui.draw(draw)
 * }
 * ```
 *
 * Everything platform-independent — the graph, the element tree, the pointer, the viewport inputs,
 * `compose` / `update` / `draw` — lives in [DukeApp]. What is left here is only what is actually
 * about Mechanica: the camera mapping, the mouse, and how a `Drawer` paints a box and a string.
 * `compose` may be called **once**; see [DukeApp].
 *
 * A subclass extends the same four things it already overrides — which is how a game with its own
 * shaders customises drawing, rather than by supplying a separate backend object:
 *
 * ```kotlin
 * class NeonUI : MechanicaUI<NeonStyle>(StyleFactory { NeonStyle() }) {
 *     override fun fill(node: NodeView<NeonStyle>, style: NeonStyle, renderer: Drawer) {
 *         if (style.glowLevel > 0.0) drawGlow(node, style, renderer) else super.fill(node, style, renderer)
 *     }
 * }
 * ```
 *
 * **A font requires an OpenGL context**, so construct this after `Game.create()` / inside a
 * `Scene`, never in a top-level `val`.
 */
open class MechanicaUI<S>(
    styles: StyleFactory<S>,
    private val camera: UICamera = Game.ui,
    /**
     * Shared with the draw path deliberately: layout decides how tall a label is by wrapping it,
     * and [drawText] has to reproduce the identical breaks or the box and its contents disagree.
     * Two metrics objects with the same settings would agree today and nothing would keep them
     * that way.
     */
    val metrics: MechanicaTextMetrics = MechanicaTextMetrics(),
) : DukeApp<S, Drawer>(styles, metrics) where S : Any, S : HasBackground, S : HasText {

    /**
     * Layout coordinates are y-down with the origin at the top-left of the camera, which is what
     * makes `top { 0.0 }` mean "the top of the screen" and lets a list stack downwards by adding.
     * The flip back to Mechanica's y-up camera is the negative viewport height below, and it
     * happens once, in the draw walk.
     */
    override val window: Window
        get() = Window(-camera.width / 2.0, -camera.height / 2.0, camera.width, camera.height)

    override val viewport: Viewport
        get() = Viewport(0.0, 0.0, camera.width, -camera.height)

    private val mouse = Mouse.create()

    override val input = object : DukeInput {
        override fun isClickDown(): Boolean {
            return mouse.MB1.isDown
        }

        override fun isRightClickDown(): Boolean {
            return mouse.MB2.isDown
        }

        override val pointerX: Double
            get() = mouse.ui.x
        override val pointerY: Double
            get() = mouse.ui.y
        override val scrollDistance: Double
            get() = mouse.scroll.distance
    }

    // ── Clipping ──────────────────────────────────────────────────────────────────────────────

    /**
     * The clips currently in force, innermost last.
     *
     * A stack rather than a single rectangle because [popClip] has to *restore*, and GL scissor
     * state is a single register with no notion of nesting. DukeCompose has already intersected
     * each rectangle with its ancestors, so what is stored here is the final answer at each depth
     * and restoring is a plain re-apply.
     */
    private val clips = ArrayList<ClipRect>()

    override fun pushClip(clip: ClipRect, renderer: Drawer) {
        clips += clip
        applyScissor(clip)
    }

    override fun popClip(renderer: Drawer) {
        clips.removeAt(clips.lastIndex)
        val enclosing = clips.lastOrNull()
        if (enclosing == null) MechanicaFactory.scissorFactory.disableScissor()
        else applyScissor(enclosing)
    }

    /**
     * Convert a clip from UI-camera coordinates to framebuffer pixels and hand it to GL.
     *
     * The camera spans `±width/2` about the origin and is y-up, which is also the scissor
     * rectangle's convention, so this is a scale and an offset with no flip — the y-down of layout
     * was already undone by the negative [viewport] height before a [ClipRect] was built.
     *
     * Rounded outwards rather than to nearest: a half-covered boundary pixel is better kept than
     * dropped, since the alternative shows a one-pixel gap between a panel's border and the content
     * it is clipping.
     */
    private fun applyScissor(clip: ClipRect) {
        val surface = Game.surface
        val w = camera.width
        val h = camera.height
        if (w == 0.0 || h == 0.0) return

        val x0 = floor((clip.minX + w / 2.0) / w * surface.width).toInt()
        val y0 = floor((clip.minY + h / 2.0) / h * surface.height).toInt()
        val x1 = ceil((clip.maxX + w / 2.0) / w * surface.width).toInt()
        val y1 = ceil((clip.maxY + h / 2.0) / h * surface.height).toInt()

        MechanicaFactory.scissorFactory.enableScissor(x0, y0, x1 - x0, y1 - y0)
    }

    /**
     * Draw the UI, leaving the scissor test off however the walk ended.
     *
     * Belt and braces: [popClip] already balances [pushClip], but GL scissor state is global, so
     * an exception part-way through a clipped subtree would otherwise leave the *world* camera
     * clipped to a UI panel — a bug that shows up three systems away as "the game stopped
     * rendering" with nothing pointing back here.
     */
    override fun draw(renderer: Drawer) {
        try {
            super.draw(renderer)
        } finally {
            if (clips.isNotEmpty()) {
                clips.clear()
                MechanicaFactory.scissorFactory.disableScissor()
            }
        }
    }

    // ── Drawing ───────────────────────────────────────────────────────────────────────────────
    //
    // Geometry arrives already in window coordinates — the viewport transform lives in
    // DukeCompose's draw walk — so nothing here sees layout coordinates. Extents arrive *signed*:
    // the UI camera is y-up while layout is y-down, so `height` is normally negative.
    // `Drawer.rectangle` handles that, and the text placement below is iteration A's formula
    // unchanged for the same reason.

    /**
     * One [Text] per text element, plus what it was last built from.
     *
     * `Text` rebuilds its vertex buffers in its constructor and again on every `string` / `font`
     * assignment, so allocating or reassigning one per frame would re-tesselate every glyph in the
     * UI every frame. Caching the inputs means a static label costs nothing after its first frame,
     * and a re-wrap happens exactly when the text, the font or the laid-out width changed.
     *
     * Keyed by element identity, which is stable because composition happens once; **this leaks one
     * entry per text element retired by a recomposition**, and wants clearing from reconciliation
     * when that exists.
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

        // The font layout measured with, so that what is drawn matches what was laid out. Any other
        // FontRef is one DukeCompose could measure but Mechanica cannot draw, which is silent
        // misalignment rather than an error — worth failing loudly for.
        val content = node.element.content ?: return
        val font = node.graph.fontOf(content) as? MechanicaFont
            ?: throw IllegalStateException(
                "${node.element} was laid out with a ${node.graph.fontOf(content)::class.simpleName}, " +
                        "which MechanicaUI cannot draw; use a MechanicaFont"
            )

        // Mechanica's `Text` breaks on explicit \n and nothing else, so the wrap layout used to
        // decide this element's height has to be materialised into the string. Without this the box
        // is three lines tall and the text is one long line running out the side of it.
        //
        // Measured in layout units, not `node.width`: the latter has been through the viewport
        // transform and is signed, while the metrics wrapped in the space layout works in.
        val width = node.graph.widthOf(node.element)

        val cached = texts.getOrPut(node.element) { Cached(Text(text, font.font)) }
        if (cached.source != text || cached.width != width || cached.font !== font) {
            val model = cached.model
            // Both setters re-tesselate, so only assign on an actual change. This is the draw-side
            // twin of RefEquality.EQUALS on the text node.
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
     * richer style type overrides it, checks its own capability — a glow level, a shader handle —
     * and falls back to `super.fill(...)` for the ordinary case, without reimplementing text
     * handling or the wrap cache. Overriding `drawCustom` and routing through `kind("…")` is the
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

/**
 * The common case: Mechanica's own style, and everything a `Drawer` can draw.
 *
 * A function rather than a default type argument, which Kotlin has no way to express. An
 * application that extends [MechanicaStyle] — or implements [HasBackground] and [HasText] some
 * other way — calls the constructor with its own factory instead.
 */
fun MechanicaUI(
    camera: UICamera = Game.ui,
    metrics: MechanicaTextMetrics = MechanicaTextMetrics(),
): MechanicaUI<MechanicaStyle> = MechanicaUI(StyleFactory { MechanicaStyle() }, camera, metrics)
