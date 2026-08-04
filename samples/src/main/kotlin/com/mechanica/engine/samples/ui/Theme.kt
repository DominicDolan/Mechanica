package com.mechanica.engine.samples.ui

import com.dubulduke.dsl.ambientOf
import com.dubulduke.layout.Marker
import com.mechanica.engine.compose.HasBackground
import com.mechanica.engine.compose.HasText

/**
 * The sample's stylesheet: a palette, an ambient holding it, and a file of styler functions.
 *
 * This is `dsl-and-engine.md` §4.1–4.3 in practice, and the notable thing is how little machinery
 * it needs. A "class" is an **extension function on the style type**, so composing two of them is
 * calling two functions and overriding one is a later assignment. There is no registration step, no
 * selector language, no specificity rules and no matching pass — and unlike a stylesheet, a typo is
 * a compile error and renaming a style is a refactor rather than a search.
 *
 * The theme is an [com.dubulduke.dsl.Ambient], which is what makes it swappable per subtree rather
 * than global. That also collapses three features into one: theming, contextual styling ("a card
 * inside a card looks different") and inheritance of text colour are all the same mechanism.
 *
 * ### The one rule
 *
 * Ambients resolve when an element is **built**, so `provide` does not re-run anything. A palette
 * that changes at runtime must be held in a signal and read *inside* a styler, not swapped in the
 * ambient after composition. Same distinction as `text("…")` versus `text { … }`.
 */
data class Palette(
    val background: Long,
    val surface: Long,
    val row: Long,
    val rowHovered: Long,
    val rowPressed: Long,
    val rowFocused: Long,
    val rowSelected: Long,
    val accent: Long,
    val warning: Long,
    val text: Long,
    val textBright: Long,
)

val DarkPalette = Palette(
    background = 0x1E1E24FF,
    surface = 0x2A2A33FF,
    row = 0x343440FF,
    rowHovered = 0x505066FF,
    rowPressed = 0x606078FF,
    rowFocused = 0x7A5AF5FF,
    rowSelected = 0x4C6EF5FF,
    accent = 0x8CE99AFF,
    warning = 0xFFA94DFF,
    text = 0xA9ECEFFF,
    textBright = 0xFFFFFFFF,
)

/** A lighter surface, to show a subtree re-themed by nothing more than a `provide`. */
val SlatePalette = DarkPalette.copy(
    surface = 0x33333EFF,
    row = 0x3E3E4CFF,
)

/** The palette in force. Read with `ambient(Theme)`, overridden with `provide(Theme to …)`. */
val Theme = ambientOf("theme") { DarkPalette }

/**
 * Marks a list row, so a control nested inside one can style itself differently without the row
 * having to know anything about it.
 *
 * A typed `val`, not a string: `hasAncestor(ListRow)` survives a rename and cannot be typo'd, which
 * `".row &"` manages neither of. Resolved once when the element is built — ancestry cannot change,
 * so there is no node behind it and nothing to invalidate.
 */
val ListRow = Marker("listRow")

// ── The stylesheet ────────────────────────────────────────────────────────────────────────────
//
// Generic in the capability rather than written against MechanicaStyle, so each one states exactly
// what it needs and will not compile against a style that lacks it.

fun <S : HasBackground> S.panel(palette: Palette) {
    color.set(palette.surface)
    radius = 8.0
}

fun <S : HasBackground> S.rowSurface(palette: Palette, state: RowState) {
    color.set(
        when (state) {
            RowState.SELECTED -> palette.rowSelected
            RowState.PRESSED -> palette.rowPressed
            RowState.FOCUSED -> palette.rowFocused
            RowState.HOVERED -> palette.rowHovered
            RowState.NORMAL -> palette.row
        }
    )
    radius = 6.0
}

/**
 * State variants without pseudo-classes: an enum the styler branches on.
 *
 * The ordering *is* the precedence, stated once here rather than repeated at every call site — the
 * job `:hover` / `:focus` specificity does in CSS, done by an ordinary `when`.
 */
enum class RowState { SELECTED, PRESSED, FOCUSED, HOVERED, NORMAL }

fun <S : HasText> S.label(palette: Palette, bright: Boolean = false) {
    textColor.set(if (bright) palette.textBright else palette.text)
    textAlignment.set(0.0, 0.5)
}

fun <S : HasText> S.centred() {
    textAlignment.set(0.5, 0.5)
}

fun <S : HasBackground> S.chipOf(colour: Long, size: Double) {
    color.set(colour)
    radius = size / 2.0
}
