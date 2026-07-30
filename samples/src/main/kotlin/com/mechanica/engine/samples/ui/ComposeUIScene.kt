package com.mechanica.engine.samples.ui

import com.dubulduke.dsl.ElementScope
import com.dubulduke.dsl.below
import com.dubulduke.dsl.box
import com.dubulduke.dsl.column
import com.dubulduke.dsl.fillWidth
import com.dubulduke.dsl.label
import com.dubulduke.dsl.row
import com.dubulduke.dsl.square
import com.dubulduke.layout.isHovered
import com.dubulduke.layout.isPressed
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
private const val ICON = 0x8CE99AFFL
private const val BADGE = 0xFFA94DFFL
private const val TEXT = 0xE9ECEFFFL
private const val TEXT_BRIGHT = 0xFFFFFFFFL

private val ITEMS = listOf(
    "Inbox" to "A short one.",
    "Drafts" to "This row carries a much longer body, so it wraps onto several lines and stands " +
        "taller than its neighbours, which pushes every row below it further down the panel.",
    "Sent" to "Another short one.",
    "Archive" to "Medium length, long enough to wrap once the window is narrow, but not before.",
    "Spam" to "Short.",
)

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

    /** Which row is selected. Written by a click handler, read by style only. */
    private val selected = ui.signal(0.0, "selected")

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
                style { color.set(PANEL) }
                layout {
                    left { 0.0 }; width { 160.0 + 40.0 * sin(clock.value) }
                    top { 0.0 }; height { parent.height }
                }

                label("DukeCompose", titleFont, id = "brand") {
                    style { textColor.set(TEXT_BRIGHT); textAlignment.set(0.5, 0.5) }
                    layout { top { 24.0 }; height { 40.0 } }
                }
            }

            // Sized *by* its rows: width is whatever is left beside the sidebar, height is the
            // bottom of the last child plus padding. Nothing here counts rows.
            column(gap = 8.0, id = "panel") {
                style { color.set(PANEL); radius = 8.0 }
                layout {
                    left { query("sidebar").right + 16.0 }
                    right { parent.right - 16.0 }
                    top { 16.0 }
                    height { if (children.isEmpty) 0.0 else children.last.bottom - top + 12.0 }
                }

                for ((i, item) in ITEMS.withIndex()) {
                    val (title, body) = item
                    row(gap = 8.0, id = "row$i") {
                        onClick { selected.value = i.toDouble() }
                        style {
                            color.set(
                                when {
                                    selected.value.toInt() == i -> ROW_SELECTED
                                    graph.isPressed(element) -> ROW_PRESSED
                                    graph.isHovered(element) -> ROW_HOVERED
                                    else -> ROW
                                }
                            )
                            radius = 6.0
                        }
                        // Inherits left/right/top from the column; says only how tall it is, and
                        // that comes from its own children.
                        layout {
                            fillWidth(12.0)
                            below(8.0)
                            height { children.maxOf { it.height } + 20.0 }
                        }

                        chip(ICON, 28.0, id = "icon$i") {
                            layout { centerY { parent.centerY }; left { parent.left + 4.0 } }
                        }

                        label("$title - $body", bodyFont, id = "label$i") {
                            style { textColor.set(TEXT) }
                            // Width is everything between the icon and the badge; height comes
                            // from the wrapped text at that width. The dependency runs
                            // vertical-size → horizontal-size, which is what makes wrapping work.
                            layout {
                                right { query("badge$i").left - 12.0 }
                                centerY { parent.centerY }
                            }
                        }

                        chip(BADGE, 16.0, id = "badge$i") {
                            layout { right { parent.right - 12.0 }; centerY { parent.centerY } }
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
        ui.update()
    }

    override fun render(draw: Drawer) = ui.draw(draw)
}
