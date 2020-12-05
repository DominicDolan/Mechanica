package com.mechanica.engine.graphics

import com.mechanica.engine.context.loader.GLDrawerLoader
import com.mechanica.engine.models.Model
import java.nio.IntBuffer


class DrawBaseVertex(private val glDrawCommands: GLDrawerLoader) : ElementsDrawCommand, MultiElementsDrawCommand {
    var baseVertex: Int = 0

    override fun elements(vertexCount: Int) {
        glDrawCommands.drawElementsBaseVertex(vertexCount, baseVertex)
    }

    override fun elements(models: Array<Model>) {
        glDrawCommands.multiDrawElementsBaseVertex(models, baseVertex)
    }
}

class MultiDrawBaseVertex(private val glDrawCommands: GLDrawerLoader) : MultiElementsDrawCommand {
    var baseVertex: IntBuffer? = null

    override fun elements(models: Array<Model>) {
        val buffer = baseVertex
        if (buffer != null) {
            glDrawCommands.multiDrawElementsBaseVertex(models, buffer)
        }
    }
}

class DrawRangeBaseVertex(
        private val glDrawCommands: GLDrawerLoader,
        private val drawRange: DrawRange): ElementsDrawCommand {
    internal var baseVertex: Int = 0

    override fun elements(model: Model) {
        elements(model.vertexCount)
    }

    override fun elements(vertexCount: Int) {
        glDrawCommands.drawRangeElementsBaseVertex(vertexCount, drawRange.start, drawRange.end, baseVertex)
    }

}
