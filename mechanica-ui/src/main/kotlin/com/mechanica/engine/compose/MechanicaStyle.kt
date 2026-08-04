package com.mechanica.engine.compose

import com.cave.library.color.VariableColor
import com.cave.library.vector.vec2.MutableVector2

/**
 * What a Mechanica `Drawer` can be told about an element, split into capabilities.
 *
 * DukeCompose has no style type of its own — `dsl-and-engine.md` §4.5, the style type is supplied
 * by the application, because platforms differ in capability and a fixed framework class must take
 * either the union (properties that silently no-op) or the intersection (capabilities unreachable).
 *
 * These interfaces are how the layering works. **Mechanica declares what Mechanica can draw**;
 * an application declares whatever else *it* can draw, and a component in between constrains itself
 * to exactly the capabilities it uses:
 *
 * ```kotlin
 * // in the game
 * interface HasGlow { var glowLevel: Double }
 * class NeonStyle : MechanicaStyle(), HasGlow { override var glowLevel = 0.0 }
 *
 * // a component that needs a background and glow, and nothing else
 * fun <S> ElementScope<S>.neonPanel(body: ElementScope<S>.() -> Unit)
 *     where S : Any, S : HasBackground, S : HasGlow { … }
 *
 * // and a UI that knows how to render it
 * class NeonUI : MechanicaUI<NeonStyle>(StyleFactory { NeonStyle() }) {
 *     override fun fill(node: NodeView<NeonStyle>, style: NeonStyle, renderer: Drawer) {
 *         if (style.glowLevel > 0.0) drawGlow(node, style, renderer) else super.fill(node, style, renderer)
 *     }
 * }
 * ```
 *
 * `neonPanel` will not compile against a style type lacking glow — a compile error rather than a
 * blank rectangle. That is the payoff §4.5 predicts, and it is why the capabilities are interfaces
 * rather than fields on one class.
 *
 * **The hard rule holds throughout: anything layout reads must not live here.** Font and text
 * content are framework-owned and go through `text(string, font)`, because a style object cannot
 * have slot identity and so anything layout read from one would be invisible to invalidation.
 * Iteration A's `Style.textFormat.font` was on the wrong side of that line.
 */
interface HasBackground {
    /** Background fill. Alpha 0 draws nothing. */
    val color: VariableColor

    /** Corner radius, in layout units. */
    var radius: Double

    /** False skips this element's own drawing. Its children still draw. */
    var isVisible: Boolean
}

interface HasText {
    val textColor: VariableColor

    /**
     * Where text sits inside the element's box, normalised. `(0, 0)` is top-left, `(0.5, 0.5)`
     * centred. Text is *positioned* by this, never sized by it — the box comes from layout.
     */
    val textAlignment: MutableVector2
}

/**
 * The batteries-included style: everything Mechanica's `Drawer` can be told, and nothing else.
 *
 * `open`, so an application can extend it rather than reimplement the capabilities. An application
 * needing something Mechanica cannot draw implements the interfaces directly instead — nothing here
 * is privileged.
 */
open class MechanicaStyle : HasBackground, HasText {
    override val color: VariableColor = VariableColor.rgba(1.0, 1.0, 1.0, 0.0)
    override var radius: Double = 0.0
    override var isVisible: Boolean = true
    override val textColor: VariableColor = VariableColor.rgba(0.9, 0.9, 0.9, 1.0)
    override val textAlignment: MutableVector2 = MutableVector2.create(0.0, 0.0)
}
