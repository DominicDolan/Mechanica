package com.mechanica.engine.memory

import org.lwjgl.system.MemoryStack
import java.nio.FloatBuffer
import java.nio.IntBuffer

class RectangleFloatBuffers(stack: MemoryStack) {
    val x: FloatBuffer = stack.floats(0f)
    val y: FloatBuffer = stack.floats(0f)
    val width: FloatBuffer = stack.floats(0f)
    val height: FloatBuffer = stack.floats(0f)
}