package com.mechanica.engine.graphics

import com.mechanica.engine.context.loader.GLDrawerLoader


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

    override fun arrays(vertexCount: Int) {
        glDrawCommands.drawArraysInstanced(vertexCount, instanceCount)
    }

    override fun elements(vertexCount: Int) {
        glDrawCommands.drawElementsInstanced(vertexCount, instanceCount)
    }

    private inner class BaseVertex : ElementsDrawCommand {
        var baseVertex: Int = 0

        override fun elements(vertexCount: Int) {
            glDrawCommands.drawElementsInstancedBaseVertex(vertexCount, instanceCount, baseVertex)
        }
    }
}

class DrawInstancedBaseInstance(
        private val glDrawCommands: GLDrawerLoader,
        private val drawInstanced: DrawInstanced) : DrawCommands {
    internal var baseInstance: Int = 0

    override fun arrays(vertexCount: Int) {
        glDrawCommands.drawArraysInstancedBaseInstance(vertexCount, drawInstanced.instanceCount, baseInstance)
    }

    override fun elements(vertexCount: Int) {
        glDrawCommands.drawElementsInstancedBaseInstance(vertexCount, drawInstanced.instanceCount, baseInstance)
    }

}