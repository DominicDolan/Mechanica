package com.mechanica.engine.samples.ui

import com.dubulduke.dsl.ElementScope
import com.dubulduke.dsl.below
import com.dubulduke.dsl.box
import com.dubulduke.dsl.column
import com.dubulduke.dsl.fillWidth
import com.dubulduke.dsl.flexRow
import com.dubulduke.dsl.forEach
import com.dubulduke.dsl.label
import com.dubulduke.dsl.scrollArea
import com.dubulduke.dsl.square
import com.dubulduke.dsl.stackAcross
import com.dubulduke.layout.isFocused
import com.dubulduke.layout.isHovered
import com.dubulduke.layout.isPressed
import com.dubulduke.layout.leftOf
import com.dubulduke.layout.widthOf
import com.mechanica.engine.compose.HasBackground
import com.mechanica.engine.compose.MechanicaFont
import com.mechanica.engine.compose.MechanicaUI
import com.mechanica.engine.config.configure
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.resources.defaultFont
import com.mechanica.engine.scenes.scenes.Scene
import com.mechanica.engine.shaders.text.Font
import kotlin.math.sin

/**
 * DukeCompose running in Mechanica, written in the DSL.
 *
 * What this is meant to show, beyond "boxes appear":
 *
 *  - **Composition happens once**, in `init`. The clock, the selection and window resizes all
 *    arrive as graph *inputs*, and only the slots that actually depend on one recompute.
 *  - **Container defaults.** Each row says only how tall it is; `column` supplies its left, right
 *    and top, and a child overriding one of those keeps the rest.
 *  - **The panel is shrink-wrapped to its rows**, whose heights come from wrapped text — the case
 *    `layout-design.md` §8.2 exists to prove comes out cycle-free with no governor.
 *  - **Row height comes from wrapped text.** `label` measures at whatever width it ends up with,
 *    so narrowing the window re-wraps a row and re-heights everything below it.
 *  - **Style is a sink.** The selection highlight and hover tint change colours without
 *    recomputing a single layout slot.
 *  - **Events are graph inputs.** `onClick` is a retained handler, which is what makes it fire
 *    under compose-once; a polled check would never run.
 */
fun main() {
    Game.configure {
        setViewport(height = 720.0)
        setFullscreen(false)
        setStartingScene { ComposeUIScene() }
    }
    Game.loop()
}

private const val BACKGROUND = 0x1E1E24FFL
private const val PANEL = 0x2A2A33FFL
private const val ROW = 0x343440FFL
private const val ROW_HOVERED = 0x505066FFL
private const val ROW_PRESSED = 0x606078FFL
private const val ROW_SELECTED = 0x4C6EF5FFL
private const val ROW_FOCUSED = 0x7A5AF5FFL
private const val ICON = 0x8CE99AFFL
private const val BADGE = 0xFFA94DFFL
private const val TEXT = 0xA9ECEFFFL
private const val TEXT_BRIGHT = 0xFFFFFFFFL

/** The slider thumb's diameter. Named because both its layout and its radius want it. */
private const val THUMB = 18.0

/** One row of the list. `id` is the reconciliation key. */
private data class Row(val id: String, val title: String, val body: String)

private val ITEMS = listOf(
    "Inbox" to "A short one.",
    "Drafts" to "This row carries a much longer body, so it wraps onto several lines and stands " +
        "taller than its neighbours, which pushes every row below it further down the panel.",
    "Sent" to "Another short one.",
    "Archive" to "Medium length, long enough to wrap once the window is narrow, but not before.",
    "Spam" to "Short.",
).mapIndexed { i, (title, body) -> Row("row$i", title, body) }

/**
 * A reusable component, constrained to the one capability it uses.
 *
 * `dsl-and-engine.md` §4.5's payoff in miniature: it compiles against any style type with a
 * background — Mechanica's own, or a game's extended one — and will not compile against a style
 * without one. Under iteration C's builder chain a helper setting both layout *and* style could
 * not be written at all.
 */
private fun <S> ElementScope<S>.chip(
    colour: Long,
    size: Double,
    id: String? = null,
    body: ElementScope<S>.() -> Unit = {},
) where S : Any, S : HasBackground =
    box(id) {
        style { color.set(colour); radius = size / 2.0 }
        layout { width { size }; square() }
        body()
    }

class ComposeUIScene : Scene() {

    private val ui = MechanicaUI()

    /** Seconds since start. A signal, so the graph sees time exactly as it sees a resize. */
    private val clock = ui.signal(0.0, "clock")

    /** Which row is selected, by key. Written by a click handler, read by style only. */
    private val selected = ui.refSignal<Any?>(null, "selected")

    /**
     * The list itself — plain application state, not a graph node.
     *
     * Reconciliation reads it once per frame and compares keys, so structure needs no signal.
     * Replaced wholesale rather than mutated: an unchanged list is caught by an identity check
     * before a single key is looked at, and a list mutated in place would defeat that.
     */
    private var rows: List<Row> = ITEMS

    private var added = 0

    /** Slider position, 0..1. Written by the drag, read by layout — an input like any other. */
    private val density = ui.signal(0.35, "density")

    private val titleFont = MechanicaFont(Font.defaultFont, 26.0)
    private val bodyFont = MechanicaFont(Font.defaultFont, 18.0)

    init {
        ui.compose {
            style { color.set(BACKGROUND) }
            layout {
                left { 0.0 }; width { ui.viewportWidth }
                top { 0.0 }; height { ui.viewportHeight }
            }

            // A sidebar whose width breathes, so a driven input visibly re-lays out what sits
            // beside it.
            column(gap = 8.0, id = "sidebar") {
                style { panel(ambient(Theme)) }
                layout {
                    left { 0.0 }; width { 160.0 + 40.0 * sin(clock.value) }
                    top { 0.0 }; height { parent.height }
                }

                label("DukeCompose", titleFont, id = "brand") {
                    val palette = ambient(Theme)
                    style { label(palette, bright = true); centred() }
                    layout { top { 24.0 }; height { 40.0 } }
                }

                // Structural change from a click handler, which is why reconciliation runs
                // after input dispatch and before anything measures.
                label("+ add a row", bodyFont, id = "add") {
                    interactive()
                    // Focusable, and nothing more is needed to make Enter work: activation falls
                    // through to the click handler.
                    focusable()
                    onClick {
                        rows = rows + Row("added${added++}", "New", "Added at runtime.")
                    }
                    val palette = ambient(Theme)
                    style {
                        label(
                            palette,
                            bright = graph.isHovered(element) || graph.isFocused(element),
                        )
                        centred()
                    }
                    layout { fillWidth(12.0); below(16.0); height { 28.0 } }
                }

                // A slider, which is what pointer capture is for. Grab the thumb and keep the
                // button down: the pointer leaves the thumb within a few pixels of any real drag,
                // and without capture the gesture would die exactly there. While captured, no
                // other element sees hover, so sweeping across the list does not light it up.
                box(id = "track") {
                    style { color.set(ROW); radius = 4.0 }
                    layout { fillWidth(12.0); below(24.0); height { 8.0 } }

                    box(id = "thumb") {
                        // The handler works in layout coordinates — the same space `left { }` does
                        // — so it is a plain reading of where the pointer is along the track.
                        onDrag { x, _ ->
                            val track = tree.query("track")
                            val usable = graph.widthOf(track) - THUMB
                            if (usable > 0.0) {
                                val local = x - graph.leftOf(track) - THUMB / 2.0
                                density.value = (local / usable).coerceIn(0.0, 1.0)
                            }
                        }
                        style {
                            color.set(if (graph.isPressed(element)) TEXT_BRIGHT else BADGE)
                            radius = THUMB / 2.0
                        }
                        // Position is derived from the signal the drag writes; the drag never
                        // moves the element directly. Events are inputs, geometry is derived —
                        // the same one-way flow as everything else here.
                        layout {
                            left { parent.left + density.value * (parent.width - THUMB) }
                            width { THUMB }
                            centerY { parent.centerY }
                            height { THUMB }
                        }
                    }
                }

                label({ "row gap: ${(4.0 + 16.0 * density.value).toInt()}" }, bodyFont, id = "gapLabel") {
                    style { textColor.set(TEXT); textAlignment.set(0.5, 0.5) }
                    layout { fillWidth(12.0); below(20.0); height { 24.0 } }
                }
            }

            // Sized *by* its rows: width is whatever is left beside the sidebar, height is the
            // bottom of the last child plus padding. Nothing here counts rows.
            //
            // `provide` wraps it so the panel and everything in it uses the slate palette. Note
            // this is a composition-time decision: swapping the ambient later would not re-run
            // anything, because the descendants have already read it.
            provide(Theme to SlatePalette) {
                column(gap = 8.0, id = "panel") {
                    // A different palette for this subtree and nothing else, which is what makes the
                    // theme an ambient rather than a global. Everything below reads `ambient(Theme)`
                    // and gets this one; the sidebar keeps the default.
                    style { panel(ambient(Theme)) }
                    layout {
                        left { query("sidebar").right + 16.0 }
                        right { parent.right - 16.0 }
                        top { 16.0 }
                        height { if (children.isEmpty) 0.0 else children.last.bottom - top + 12.0 }
                    }

                    // The rows are keyed and dynamic. Clicking one's badge removes it; the sidebar
                    // button puts one back. Composition still happens exactly once — only this
                    // region rebuilds, and only when its keys actually move.
                    //
                    // And the list scrolls, which took no new mechanism: the area clips its subtree,
                    // and the content's `top` reads a signal the wheel writes. Add rows past the
                    // area's height and the wheel moves them; the ones that go off the top are
                    // neither drawn nor clickable, while their slots stay the expressions they were.
                    scrollArea(id = "list", speed = 30.0) {
                        layout {
                            fillWidth(0.0)
                            top { parent.top }
                            // Stated rather than measured, unlike the panel around it. A scroll area
                            // sized by its content would have nothing to scroll — that is what makes
                            // this the one place in the sample a fixed number is the right answer.
                            height { 340.0 }
                        }

                        content(gap = 8.0) {
                            forEach({ rows }, key = { it.id }) { item ->
                                // The item *is* the row, so it has to become a row container itself —
                                // `row(gap) { }` would wrap it in a second element.
                                stackAcross(gap = 8.0)
                                // Tab or arrow into a row and Enter selects it. Focus is retained
                                // state, so it survives the mouse moving away — unlike hover, which
                                // is why both are read here rather than one standing in for the other.
                                focusable()
                                marker(ListRow)
                                onClick { selected.value = item.key }
                                // The whole appearance is one call into Theme.kt. Precedence between
                                // selected / pressed / focused / hovered is stated once there rather
                                // than repeated at every row that wants it.
                                val palette = ambient(Theme)
                                style {
                                    rowSurface(
                                        palette,
                                        when {
                                            selected.value == item.key -> RowState.SELECTED
                                            graph.isPressed(element) -> RowState.PRESSED
                                            graph.isFocused(element) -> RowState.FOCUSED
                                            graph.isHovered(element) -> RowState.HOVERED
                                            else -> RowState.NORMAL
                                        },
                                    )
                                }
                                // Inherits left/right/top from the column; says only how tall it is,
                                // and that comes from its own children.
                                layout {
                                    fillWidth(12.0)
                                    below(8.0)
                                    height { children.maxOf { it.height } + 20.0 }
                                }

                                chip(ICON, 28.0) {
                                    layout { centerY { parent.centerY }; left { parent.left + 4.0 } }
                                }

                                // `{ … }` rather than a plain string: a surviving row's body never
                                // runs again, so a captured string would freeze at whatever it was
                                // when the row first appeared. Read through `item.value` and it tracks.
                                label({ "${item.value.title} - ${item.value.body}" }, bodyFont) {
                                    style { textColor.set(TEXT) }
                                    // Width is everything between the icon and the badge; height comes
                                    // from the wrapped text at that width. The dependency runs
                                    // vertical-size → horizontal-size, which is what makes wrapping
                                    // work. `next` rather than an id, so it survives a reorder.
                                    layout {
                                        right { next!!.left - 12.0 }
                                        centerY { parent.centerY }
                                    }
                                }

                                chip(BADGE, 16.0) {
                                    interactive()
                                    onClick { rows = rows.filter { r -> r.id != item.key } }
                                    layout { right { parent.right - 12.0 }; centerY { parent.centerY } }
                                }
                            }
                        }
                    }

                    // A flex row. The icon and the badge take widths of their own; the two labels
                    // split what is left, 1:2. Nothing here computes a width — the row hoists the
                    // leftover space into a node both labels read, which is why competing siblings
                    // are a plain DAG rather than the cycle they look like (`layout-design.md` §6.2).
                    //
                    // Its height is stated rather than measured, and that is not laziness: items
                    // fill the row's cross axis by default, so a row whose height came from its
                    // children would be a genuine self-cycle.
                    flexRow(gap = 8.0, id = "flex") {
                        style { color.set(ROW); radius = 6.0 }
                        layout {
                            fillWidth(12.0)
                            below(8.0)
                            height { 48.0 }
                        }

                        fixed({ 28.0 }, id = "flexIcon") {
                            style { color.set(ICON); radius = 14.0 }
                            layout { height { 28.0 }; centerY { parent.centerY } }
                        }

                        grow(1.0, id = "flexOne") {
                            text("one share", bodyFont)
                            style { textColor.set(TEXT); textAlignment.set(0.0, 0.5); }
                        }

                        grow(2.0, id = "flexTwo") {
                            text("two shares of the leftover space", bodyFont)
                            style { textColor.set(TEXT); textAlignment.set(0.0, 0.5); }
                        }

                        grow(1.0, id = "flexThree") {
                            text("three shares of the leftover space", bodyFont)
                            style { textColor.set(TEXT); textAlignment.set(0.0, 0.5); }
                        }

                        fixed({ 16.0 }, id = "flexBadge") {
                            style { color.set(BADGE); radius = 8.0 }
                            layout { height { 16.0 }; centerY { parent.centerY } }
                        }
                    }
                }
            }
        }
    }

    private var elapsed = 0.0

    override fun update(delta: Double) {
        elapsed += delta
        clock.value = elapsed
        ui.update(delta)
    }

    override fun render(draw: Drawer) = ui.draw(draw)
}
