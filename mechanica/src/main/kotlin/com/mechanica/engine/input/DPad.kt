package com.mechanica.engine.input

import com.cave.library.vector.vec2.Vector2

class DPad(downKey: Key, upKey: Key, leftKey: Key, rightKey: Key, multiplier: Double = 1.0) {
    private val vertical = DirectionalKeys(downKey, upKey, multiplier)
    private val horizontal = DirectionalKeys(leftKey, rightKey, multiplier)

    val value: Vector2 = object : Vector2 {
        override val x: Double
            get() = horizontal.value
        override val y: Double
            get() = vertical.value

    }

    companion object {
        fun createWithWASD(multiplier: Double = 1.0): DPad {
            return DPad(Key(KeyIDs.S), Key(KeyIDs.W), Key(KeyIDs.A), Key(KeyIDs.D), multiplier)
        }

        fun createWithArrowKeys(multiplier: Double = 1.0): DPad {
            return DPad(Key(KeyIDs.DOWN), Key(KeyIDs.UP), Key(KeyIDs.LEFT), Key(KeyIDs.RIGHT), multiplier)
        }

        fun createWithWASDAndArrowKeys(multiplier: Double = 1.0): DPad {
            return DPad(Key(KeyIDs.S, KeyIDs.DOWN), Key(KeyIDs.W, KeyIDs.UP), Key(KeyIDs.A, KeyIDs.LEFT), Key(KeyIDs.D, KeyIDs.RIGHT), multiplier)
        }
    }
}