@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.mechanica.nativebackend

import com.mechanica.engine.shaders.draw.GLDrawerFactory
import com.mechanica.engine.shaders.models.Model
import com.mechanica.samples.nativegl.*

/** Draws with the current OpenGL context and currently bound buffers. */
class NativeDrawerFactory(private val mode: UInt, private val type: UInt) : GLDrawerFactory {
    override fun drawElements(vertexCount: Int) {
        if (vertexCount > 0) glDrawElements(mode, vertexCount, type, null)
    }
    override fun drawElementsBaseVertex(vertexCount: Int, baseVertex: Int) {
        if (vertexCount > 0) glDrawElementsBaseVertex(mode, vertexCount, type, null, baseVertex)
    }
    override fun drawRangeElements(vertexCount: Int, start: Int, end: Int) {
        if (vertexCount > 0) glDrawRangeElements(mode, start.toUInt(), end.toUInt(), vertexCount, type, null)
    }
    override fun drawRangeElementsBaseVertex(vertexCount: Int, start: Int, end: Int, baseVertex: Int) {
        if (vertexCount > 0) glDrawRangeElementsBaseVertex(mode, start.toUInt(), end.toUInt(), vertexCount, type, null, baseVertex)
    }
    override fun drawElementsIndirect(vertexCount: Int) {
        if (vertexCount > 0) glDrawElementsIndirect(mode, type, null)
    }
    override fun multiDrawElements(models: Array<Model>): Unit = TODO("Multi-draw is not implemented in the JVM backend either")
    override fun multiDrawElementsBaseVertex(models: Array<Model>, baseVertex: IntArray): Unit = TODO("Multi-draw is not implemented")
    override fun multiDrawElementsBaseVertex(models: Array<Model>, baseVertex: Int): Unit = TODO("Multi-draw is not implemented")
    override fun drawElementsInstanced(vertexCount: Int, instanceCount: Int) {
        if (vertexCount > 0) glDrawElementsInstanced(mode, vertexCount, type, null, instanceCount)
    }
    override fun drawElementsInstancedBaseVertex(vertexCount: Int, instanceCount: Int, baseVertex: Int) {
        if (vertexCount > 0) glDrawElementsInstancedBaseVertex(mode, vertexCount, type, null, instanceCount, baseVertex)
    }
    override fun drawElementsInstancedBaseInstance(vertexCount: Int, instanceCount: Int, baseInstance: Int) {
        if (vertexCount > 0) glDrawElementsInstancedBaseInstance(mode, vertexCount, type, null, instanceCount, baseInstance.toUInt())
    }
    override fun drawArrays(vertexCount: Int) {
        if (vertexCount > 0) glDrawArrays(mode, 0, vertexCount)
    }
    override fun drawArraysIndirect(vertexCount: Int) {
        if (vertexCount > 0) glDrawArraysIndirect(mode, null)
    }
    override fun multiDrawArrays(models: Array<Model>): Unit = TODO("Multi-draw is not implemented")
    override fun drawArraysInstanced(vertexCount: Int, instanceCount: Int) {
        if (vertexCount > 0) glDrawArraysInstanced(mode, 0, vertexCount, instanceCount)
    }
    override fun drawArraysInstancedBaseInstance(vertexCount: Int, instanceCount: Int, baseInstance: Int) {
        if (vertexCount > 0) glDrawArraysInstancedBaseInstance(mode, 0, vertexCount, instanceCount, baseInstance.toUInt())
    }
}
