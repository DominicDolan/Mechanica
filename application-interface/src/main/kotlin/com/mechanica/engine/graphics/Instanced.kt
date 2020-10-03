package com.mechanica.engine.graphics

import com.mechanica.engine.context.loader.GLDrawerLoader
import com.mechanica.engine.models.Model


class DrawInstanced(private val glDrawCommands: GLDrawerLoader) : DrawCommands {
    internal var instanceCount: Int = 1

    private val drawInstancedBaseInstance = DrawInstancedBaseInstance(glDrawCommands, this)
    private val drawInstancedBaseVertex = BaseVertex()

    fun baseInstance(baseInstance: Int) : DrawInstancedBaseInstance {
        drawInstancedBaseInstance.baseInstance = baseInstance
        return drawInstancedBaseInstance
    }

    fun baseVertex(baseVertex: Int) : ElementsDrawCommand {
        drawInstancedBaseVertex.baseVertex = baseVertex
        return drawInstancedBaseVertex
    }

    override fun arrays(model: Model) {
        glDrawCommands.drawArraysInstanced(model, instanceCount)
    }

    override fun elements(model: Model) {
        glDrawCommands.drawElementsInstanced(model, instanceCount)
    }

    private inner class BaseVertex : ElementsDrawCommand {
        var baseVertex: Int = 0
        override fun elements(model: Model) {
            glDrawCommands.drawElementsInstancedBaseVertex(model, instanceCount, baseVertex)
        }

    }
}

class DrawInstancedBaseInstance(
        private val glDrawCommands: GLDrawerLoader,
        private val drawInstanced: DrawInstanced) : DrawCommands {
    internal var baseInstance: Int = 0

    override fun arrays(model: Model) {
        glDrawCommands.drawArraysInstancedBaseInstance(model, drawInstanced.instanceCount, baseInstance)
    }

    override fun elements(model: Model) {
        glDrawCommands.drawElementsInstancedBaseInstance(model, drawInstanced.instanceCount, baseInstance)
    }

}