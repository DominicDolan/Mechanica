package com.mechanica.engine.memory

import org.lwjgl.system.MemoryStack

inline fun useMemoryStack(use: MemoryStack.() -> Unit) {
    MemoryStack.stackPush().use { use(it) }
}


fun Byte.toIntValue(): Int = when {
    (this.toInt() < 0) -> 255 + this.toInt() + 1
    else -> this.toInt()
}