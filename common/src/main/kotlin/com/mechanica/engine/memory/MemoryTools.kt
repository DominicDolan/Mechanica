package com.mechanica.engine.memory

import org.lwjgl.system.MemoryStack

inline fun useMemoryStack(use: MemoryStack.() -> Unit) {
    MemoryStack.stackPush().use { use(it) }
}
