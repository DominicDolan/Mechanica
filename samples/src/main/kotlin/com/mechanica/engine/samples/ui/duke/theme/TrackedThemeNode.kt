package com.mechanica.engine.samples.ui.duke.theme

import com.cave.library.color.Color
import com.cave.library.color.VariableColor
import com.cave.library.vector.vec2.MutableVector2
import com.cave.library.vector.vec2.Vector2
import com.mechanica.engine.shaders.text.Font


abstract class TrackedThemeNode : ThemeNode() {
    var isColorSet: Boolean = false
    override val color = TrackedColor(super.color) { isColorSet = true }

    var isVisibleSet: Boolean = false
    override var isVisible: Boolean = super.isVisible
        set(value) {
            isVisibleSet = true
            field = value
        }

    override val textFormat = TrackedTextFormat()

    var isRadiusSet: Boolean = false
    override var radius: Double = super.radius
        set(value) {
            isRadiusSet = true
            field = value
        }

    fun copySetValues(dest: Style) {
        if (isColorSet) {
            dest.color.set(color)
        }

        if (isVisibleSet) {
            dest.isVisible = isVisible
        }

        if (isRadiusSet) {
            dest.radius = radius
        }

        textFormat.copySetValues(dest.textFormat)
    }

    open class TrackedTextFormat : TextFormat() {
        var isColorSet: Boolean = false
        override val color: VariableColor = TrackedColor(super.color) { isColorSet = true }

        var isSizeSet: Boolean = false
        override var size: Double = super.size
            set(value) {
                isSizeSet = true
                field = value
            }

        var isAlignmentSet: Boolean = false
        override val alignment: MutableVector2 = TrackedVector(super.alignment) { isAlignmentSet = true }

        var isFontSet: Boolean = false
        override var font: Font? = super.font
            set(value) {
                isFontSet = true
                field = value
            }

        fun copySetValues(dest: TextFormat) {
            if (isColorSet) {
                dest.color.set(color)
            }

            if (isSizeSet) {
                dest.size = size
            }

            if (isAlignmentSet) {
                dest.alignment.set(alignment)
            }

            if (isFontSet) {
                dest.font = font
            }
        }
    }

    class TrackedColor(color: Color, private val setter: (value: Double) -> Unit) : VariableColor {
        override var r: Double = color.r
            set(value) {
                setter(value)
                field = value
            }
        override var g: Double = color.g
            set(value) {
                setter(value)
                field = value
            }
        override var b: Double = color.b
            set(value) {
                setter(value)
                field = value
            }
        override var a: Double = color.a
            set(value) {
                setter(value)
                field = value
            }
    }

    class TrackedVector(vector: Vector2, private val setter: (value: Double) -> Unit) : MutableVector2 {
        override var x: Double = vector.x
            set(value) {
                setter(value)
                field = value
            }
        override var y: Double = vector.y
            set(value) {
                setter(value)
                field = value
            }
    }
}