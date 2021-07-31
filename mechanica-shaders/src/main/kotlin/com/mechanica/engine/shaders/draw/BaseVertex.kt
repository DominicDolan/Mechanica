package com.mechanica.engine.shaders.draw

import com.mechanica.engine.shaders.models.Model
import java.nio.IntBuffer


class DrawBaseVertex(private val glDrawCommands: GLDrawerFactory) : ElementsDrawCommand, MultiElementsDrawCommand {
    var baseVertex: Int = 0

    override fun elements(vertexCount: Int) {
        glDrawCommands.drawElementsBaseVertex(vertexCount, baseVertex)
    }

    override fun elements(models: Array<Model>) {
        glDrawCommands.multiDrawElementsBaseVertex(models, baseVertex)
    }
}

class MultiDrawBaseVertex(private val glDrawCommands: GLDrawerFactory) : MultiElementsDrawCommand {
    var baseVertex: IntBuffer? = null

    override fun elements(models: Array<Model>) {
        val buffer = baseVertex
        if (buffer != null) {
            glDrawCommands.multiDrawElementsBaseVertex(models, buffer)
        }
    }
}

class DrawRangeBaseVertex(
    private val glDrawCommands: GLDrawerFactory,
    private val drawRange: DrawRange
): ElementsDrawCommand {
    internal var baseVertex: Int = 0

    override fun elements(model: Model) {
        elements(model.vertexCount)
    }

    override fun elements(vertexCount: Int) {
        glDrawCommands.drawRangeElementsBaseVertex(vertexCount, drawRange.start, drawRange.end, baseVertex)
    }

}
