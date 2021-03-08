package com.mechanica.engine.input.mouse

import com.cave.library.vector.vec2.Vector2
import com.mechanica.engine.context.callbacks.MouseHandler
import com.mechanica.engine.input.Key
import com.mechanica.engine.input.KeyID


interface Mouse {
    val MB1: Key
    val MB2: Key
    val MMB: Key
    val M4: Key
    val M5: Key
    val M6: Key
    val M7: Key
    val M8: Key
    val scroll: ScrollWheel
    val scrollDown: ScrollWheel
    val scrollUp: ScrollWheel
    val pixel: Vector2
    val world: Vector2
    val ui: Vector2
    val normalized: Vector2

    class ScrollWheel(vararg keys: KeyID): Key(*keys) {
        val distance: Double
            get() = MouseHandler.scrollY
    }

    companion object : Mouse by MouseImpl() {
        fun create(): Mouse = MouseImpl()
    }
}