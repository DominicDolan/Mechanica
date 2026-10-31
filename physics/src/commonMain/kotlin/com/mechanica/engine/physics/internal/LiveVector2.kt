package com.mechanica.engine.physics.internal

import com.cave.library.vector.vec2.MutableVector2
import com.cave.library.vector.vec2.Vector2

/** A live view; the owner supplies lifetime checks in both operations. */
internal class LiveVector2(
    private val read: () -> Vector2,
    private val write: (Double, Double) -> Unit,
) : MutableVector2 {
    override var x: Double
        get() = read().x
        set(value) {
            val current = read()
            write(value, current.y)
        }

    override var y: Double
        get() = read().y
        set(value) {
            val current = read()
            write(current.x, value)
        }

    override fun set(x: Double, y: Double) = write(x, y)

    override fun scaleXY(scale: Double) {
        val current = read()
        write(current.x * scale, current.y * scale)
    }
}
