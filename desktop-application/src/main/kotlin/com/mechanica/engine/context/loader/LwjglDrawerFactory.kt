package com.mechanica.engine.context.loader

import com.mechanica.engine.shaders.draw.GLDrawerFactory
import com.mechanica.engine.shaders.models.Model
import org.lwjgl.opengl.GL42.*
import java.nio.IntBuffer

class LwjglDrawerFactory(val mode: Int, val type: Int) : GLDrawerFactory {

    override fun drawElements(vertexCount: Int) {
        if (vertexCount > 0) {
            glDrawElements(mode, vertexCount, type, 0)
        }
    }

    override fun drawElementsBaseVertex(vertexCount: Int, baseVertex: Int) {
        if (vertexCount > 0) {
            glDrawElementsBaseVertex(mode, vertexCount, type, 0, baseVertex)
        }
    }

    override fun drawRangeElements(vertexCount: Int, start: Int, end: Int) {
        if (vertexCount > 0) {
            glDrawRangeElements(mode, start, end, vertexCount, type, 0)
        }
    }

    override fun drawRangeElementsBaseVertex(vertexCount: Int, start: Int, end: Int, baseVertex: Int) {
        if (vertexCount > 0) {
            glDrawRangeElementsBaseVertex(mode, start, end, vertexCount, type, 0, baseVertex)
        }
    }

    override fun drawElementsIndirect(vertexCount: Int) {
        if (vertexCount > 0) {
            glDrawElementsIndirect(mode, type, 0)
        }
    }

    override fun multiDrawElements(models: Array<Model>) {
        TODO("not implemented")
    }

    override fun multiDrawElementsBaseVertex(models: Array<Model>, baseVertex: IntBuffer) {
        TODO("not implemented")
    }

    override fun multiDrawElementsBaseVertex(models: Array<Model>, baseVertex: Int) {
        TODO("not implemented")
    }

    override fun drawElementsInstanced(vertexCount: Int, instanceCount: Int) {
        if (vertexCount > 0) {
            glDrawElementsInstanced(mode, vertexCount, type, 0, instanceCount)
        }
    }

    override fun drawElementsInstancedBaseVertex(vertexCount: Int, instanceCount: Int, baseVertex: Int) {
        if (vertexCount > 0) {
            glDrawElementsInstancedBaseVertex(mode, vertexCount, type, 0, instanceCount, baseVertex)
        }
    }

    override fun drawElementsInstancedBaseInstance(vertexCount: Int, instanceCount: Int, baseInstance: Int) {
        if (vertexCount > 0) {
            glDrawElementsInstancedBaseInstance(mode, vertexCount, type, 0, instanceCount, baseInstance)
        }
    }

    override fun drawArrays(vertexCount: Int) {
        if (vertexCount > 0) {
            glDrawArrays(mode, 0, vertexCount)
        }
    }

    override fun drawArraysIndirect(vertexCount: Int) {
        if (vertexCount > 0) {
            glDrawArraysIndirect(mode, 0)
        }
    }

    override fun multiDrawArrays(models: Array<Model>) {
        TODO("not implemented")
    }

    override fun drawArraysInstanced(vertexCount: Int, instanceCount: Int) {
        if (vertexCount > 0) {
            glDrawArraysInstanced(mode, 0, vertexCount, instanceCount)
        }
    }

    override fun drawArraysInstancedBaseInstance(vertexCount: Int, instanceCount: Int, baseInstance: Int) {
        if (vertexCount > 0) {
            glDrawArraysInstancedBaseInstance(mode, 0, vertexCount, instanceCount, baseInstance)
        }
    }

    private inline fun Model.isValid(action: () -> Unit) {
        if (vertexCount > 0) {
            action()
        }
    }
}