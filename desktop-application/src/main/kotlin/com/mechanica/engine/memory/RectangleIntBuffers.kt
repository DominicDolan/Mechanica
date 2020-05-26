package com.mechanica.engine.memory

import org.lwjgl.system.MemoryStack
import java.nio.IntBuffer

class RectangleIntBuffers(stack: MemoryStack) {
    val x: IntBuffer = stack.ints(0)
    val y: IntBuffer = stack.ints(0)
    val width: IntBuffer = stack.ints(0)
    val height: IntBuffer = stack.ints(0)
}