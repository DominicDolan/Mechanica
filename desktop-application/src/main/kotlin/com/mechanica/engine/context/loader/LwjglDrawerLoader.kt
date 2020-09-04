package com.mechanica.engine.context.loader

import com.mechanica.engine.models.Model
import org.lwjgl.opengl.GL42.*
import java.nio.IntBuffer

class LwjglDrawerLoader(val mode: Int, val type: Int) : GLDrawerLoader {

    override fun drawElements(model: Model) {
        model.isValid {
            glDrawElements(mode, model.vertexCount, type, 0)
        }
    }

    override fun drawElementsBaseVertex(model: Model, baseVertex: Int) {
        model.isValid {
            glDrawElementsBaseVertex(mode, model.vertexCount, type, 0, baseVertex)
        }
    }

    override fun drawRangeElements(model: Model, start: Int, end: Int) {
        model.isValid {
            glDrawRangeElements(mode, start, end, model.vertexCount, type, 0)
        }
    }

    override fun drawRangeElementsBaseVertex(model: Model, start: Int, end: Int, baseVertex: Int) {
        model.isValid {
            glDrawRangeElementsBaseVertex(mode, start, end, model.vertexCount, type, 0, baseVertex)
        }
    }

    override fun drawElementsIndirect(model: Model) {
        model.isValid {
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

    override fun drawElementsInstanced(model: Model, instanceCount: Int) {
        model.isValid {
            glDrawElementsInstanced(mode, model.vertexCount, type, 0, instanceCount)
        }
    }

    override fun drawElementsInstancedBaseVertex(model: Model, instanceCount: Int, baseVertex: Int) {
        model.isValid {
            glDrawElementsInstancedBaseVertex(mode, model.vertexCount, type, 0, instanceCount, baseVertex)
        }
    }

    override fun drawElementsInstancedBaseInstance(model: Model, instanceCount: Int, baseInstance: Int) {
        model.isValid {
            glDrawElementsInstancedBaseInstance(mode, model.vertexCount, type, 0, instanceCount, baseInstance)
        }
    }

    override fun drawArrays(model: Model) {
        model.isValid {
            glDrawArrays(mode, 0, model.vertexCount)
        }
    }

    override fun drawArraysIndirect(model: Model) {
        model.isValid {
            glDrawArraysIndirect(mode, 0)
        }
    }

    override fun multiDrawArrays(models: Array<Model>) {
        TODO("not implemented")
    }

    override fun drawArraysInstanced(model: Model, instanceCount: Int) {
        model.isValid {
            glDrawArraysInstanced(mode, 0, model.vertexCount, instanceCount)
        }
    }

    override fun drawArraysInstancedBaseInstance(model: Model, instanceCount: Int, baseInstance: Int) {
        model.isValid {
            glDrawArraysInstancedBaseInstance(mode, 0, model.vertexCount, instanceCount, baseInstance)
        }
    }

    private inline fun Model.isValid(action: () -> Unit) {
        if (vertexCount > 0) {
            action()
        }
    }
}