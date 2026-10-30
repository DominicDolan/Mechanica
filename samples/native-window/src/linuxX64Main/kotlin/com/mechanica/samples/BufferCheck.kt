@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.mechanica.samples

import com.mechanica.engine.shaders.context.MechanicaShadersInitializer
import com.mechanica.engine.shaders.text.Font
import com.mechanica.engine.shaders.text.FontFactory
import com.mechanica.engine.text.FontAtlasConfiguration
import com.mechanica.nativebackend.NativeShaderFactory
import com.mechanica.samples.nativegl.*
import kotlinx.cinterop.*

/** Checks actual GPU buffer allocation and upload, including the integer element size. */
fun checkNativeBuffers() {
    val fonts = object : FontFactory {
        override fun font(ttfFile: ByteArray, initializer: FontAtlasConfiguration.() -> Unit): Font = error("Fonts are not used by this sample")
        override fun defaultFont(): Font = error("Fonts are not used by this sample")
    }
    val factory = NativeShaderFactory(fonts)
    MechanicaShadersInitializer.initialize(factory)
    val original = intArrayOf(123, -456, 789)
    val buffer = factory.bufferFactories.arrayBufferFactory.ints(original)
    try {
        check(buffer.getExistingBufferSize() >= 12) { "Integer GPU buffer is too small" }
        val copy = IntArray(original.size)
        copy.usePinned { glGetBufferSubData(GL_ARRAY_BUFFER.toUInt(), 0, 12, it.addressOf(0)) }
        check(copy.contentEquals(original)) { "Integer GPU buffer contents do not match" }
    } finally {
        memScoped {
            val id = alloc<UIntVar>(); id.value = buffer.id.toUInt()
            glDeleteBuffers(1, id.ptr)
        }
    }
}
