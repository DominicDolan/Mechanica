package com.mechanica.engine.compose

import com.dubulduke.layout.DukeBackend
import com.dubulduke.layout.DukeHost
import com.dubulduke.layout.Element
import com.dubulduke.layout.ElementTree
import com.dubulduke.dsl.ElementScope
import com.dubulduke.dsl.RefSignal
import com.dubulduke.dsl.Signal
import com.dubulduke.dsl.StyleFactory
import com.dubulduke.dsl.refSignal
import com.dubulduke.dsl.signal
import com.dubulduke.dsl.compose
import com.dubulduke.layout.Pointer
import com.dubulduke.layout.Viewport
import com.dubulduke.layout.Window
import com.dubulduke.layout.draw
import com.dubulduke.reactivity.Graph
import com.dubulduke.reactivity.Slot
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.input.mouse.Mouse
import com.mechanica.engine.game.view.UICamera

/**
 * DukeCompose wired to a Mechanica UI camera: host, graph, element tree and draw walk in one
 * object, split into the phases `dsl-and-engine.md` §6.2 asks for.
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
 * ### Compose once
 *
 * [compose] may be called once. That is not a limitation being worked around, it is the point:
 * values change by writing graph inputs, not by re-running composition, so a UI whose *structure*
 * is fixed never recomposes at all. Iteration B's `uiEngine.build { }` ran every frame from
 * `update(delta)`; doing that here would rebuild the graph every frame, discard every memoised
 * value, and grow the node array without bound — `:reactivity` has no disposal yet.
 *
 * Structural change — a list whose length varies, a panel that appears — therefore needs
 * reconciliation, which is not built (`layout-design.md` Q10). Until it is, compose the maximum
 * structure and hide what is not wanted with `isVisible`, or rebuild the whole `MechanicaUI`.
 *
 * ### The order of the phases
 *
 * [update] pushes the camera size into the graph; [draw] pulls. Nothing is laid out in between,
 * because reading geometry *is* what evaluates it — the draw walk is the layout driver, and a
 * frame in which nothing changed recomputes nothing.
 *
 * **A font requires an OpenGL context**, so construct this after `Game.create()` / inside a
 * `Scene`, never in a top-level `val`.
 */
class MechanicaUI<S>(
    /**
     * Makes the application's style objects. There are no framework default styles (§4.5) — but
     * `MechanicaUI()` with no arguments gives you [MechanicaStyle], which is the batteries-included
     * path for an application that has not outgrown what a `Drawer` can draw.
     */
    val styles: StyleFactory<S>,
    private val camera: UICamera = Game.ui,
    val metrics: MechanicaTextMetrics = MechanicaTextMetrics(),
    /**
     * Substitutable so an application can supply its own drawing — a glow shader, a widget that is
     * not a rectangle. Defaults to one sharing [metrics], because the backend has to re-apply the
     * same wrap layout measured with, and a substitute that draws text is responsible for the same.
     */
    private val backend: DukeBackend<S, Drawer> = MechanicaBackend(metrics),
) : DukeHost where S : Any, S : HasBackground, S : HasText {

    val graph = Graph()
    val tree = ElementTree(graph, metrics)

    /**
     * The camera's size, as graph inputs.
     *
     * Two independent nodes rather than one "viewport" object, because the axes are independent
     * (`layout-design.md` §2.2): a window that gets wider but no taller must not invalidate
     * anything that only reads the height. That is measurable — a resize on the benchmark scene
     * re-runs about a quarter of the graph, not all of it.
     */
    private val viewportWidthSlot: Slot = graph.input(camera.width, "viewport.width")
    private val viewportHeightSlot: Slot = graph.input(camera.height, "viewport.height")

    /**
     * The camera's size, read through the graph.
     *
     * Plain `Double` getters rather than exposed slots, so a layout expression says
     * `width { ui.viewportWidth }` and never `g.read(...)`. The read is tracked exactly as before —
     * tracking is dynamic, so what matters is that something is evaluating when the getter runs,
     * not how the call is spelled.
     */
    val viewportWidth: Double get() = graph.read(viewportWidthSlot)
    val viewportHeight: Double get() = graph.read(viewportHeightSlot)

    /** Create an application-owned value the layout graph can watch. */
    fun signal(initial: Double = 0.0, name: String? = null): Signal = graph.signal(initial, name)

    /** Create an application-owned reference — a label's text, a selected item — the graph watches. */
    fun <T> refSignal(initial: T, name: String? = null): RefSignal<T> = graph.refSignal(initial, name)

    /**
     * Layout coordinates are y-down with the origin at the top-left of the camera, which is what
     * makes `origin { 0.0 }` mean "the top-left corner" and lets a list stack downwards by adding.
     * The flip back to Mechanica's y-up camera is the negative viewport height in [viewport], and
     * it happens once, in the draw walk.
     */
    override val window: Window
        get() = Window(-camera.width / 2.0, -camera.height / 2.0, camera.width, camera.height)

    override val viewport: Viewport
        get() = Viewport(0.0, 0.0, camera.width, -camera.height)

    override val textMetrics get() = metrics

    private var composed = false

    /** Build the UI. Once — see the class docs. Returns the root element. */
    fun compose(build: ElementScope<S>.() -> Unit): Element {
        check(!composed) { "MechanicaUI.compose may only be called once; drive changes with inputs" }
        composed = true
        return tree.compose(styles, "ui", build)
    }

    /**
     * Hover, press and click dispatch. `dsl-and-engine.md` §7.
     *
     * Hit-tests against the geometry the *last* frame settled on, which is why this is not a cycle
     * even though hover can change layout: one frame of lag is already inherent, since input is
     * sampled before anything is drawn.
     */
    val pointer = Pointer(tree)

    private val mouse = Mouse.create()

    /**
     * Push this frame's input and camera size into the graph, and dispatch any clicks.
     *
     * Order matters, and it is the order of §7.1: hit test and dispatch first, so a handler that
     * mutates application state does so *before* the layout that will read it. Writing an
     * unchanged input is free — the graph compares before it marks — so a still pointer over a
     * still window costs one hit test and no invalidation at all.
     */
    fun update() {
        if (composed) {
            val p = mouse.ui
            pointer.update(this, p.x, p.y, down = mouse.MB1.isDown)
        }
        graph.setInput(viewportWidthSlot, camera.width)
        graph.setInput(viewportHeightSlot, camera.height)
    }

    /** Lay out whatever is stale and draw. */
    fun draw(drawer: Drawer) {
        if (!composed) return
        tree.draw(this, backend, drawer)
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
