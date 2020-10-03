package com.mechanica.engine.graphics

import com.mechanica.engine.context.loader.GLDrawerLoader
import com.mechanica.engine.context.loader.GLLoader
import com.mechanica.engine.models.Model
import java.nio.IntBuffer

class GLDraw {
    private val glDrawer = GLLoader.graphicsLoader.glDrawer

    val glDraw by lazy { GLDrawCommands(glDrawer) }
    val glDrawPoints by lazy { GLDrawCommands(GLLoader.graphicsLoader.glPointDrawer) }
    val glDrawLines by lazy { GLDrawCommands(GLLoader.graphicsLoader.glLinesDrawer) }
    val glDrawLineLoop by lazy { GLDrawCommands(GLLoader.graphicsLoader.glLineLoopDrawer) }
    val glDrawLineStrip by lazy { GLDrawCommands(GLLoader.graphicsLoader.glLineStripDrawer) }

    fun drawModel(model: Model) {
        if (model.hasIndexArray) {
            glDrawer.drawElements(model)
        } else {
            glDrawer.drawArrays(model)
        }
    }

}

class GLDrawCommands(private val glDrawCommands: GLDrawerLoader) : MultiDrawCommands {

    private val instancedDraw = DrawInstanced(glDrawCommands)
    private val baseVertexDraw = DrawBaseVertex(glDrawCommands)
    private val baseVertexMultiDraw = MultiDrawBaseVertex(glDrawCommands)
    private val drawRange = DrawRange(glDrawCommands)

    override fun arrays(model: Model) {
        glDrawCommands.drawArrays(model)
    }

    override fun arrays(models: Array<Model>) {
        glDrawCommands.multiDrawArrays(models)
    }

    override fun elements(model: Model) {
        glDrawCommands.drawElements(model)
    }

    override fun elements(models: Array<Model>) {
        glDrawCommands.multiDrawElements(models)
    }

    fun instanced(instanceCount: Int): DrawInstanced {
        instancedDraw.instanceCount = instanceCount
        return instancedDraw
    }

    fun baseVertex(baseVertex: Int): DrawBaseVertex {
        baseVertexDraw.baseVertex = baseVertex
        return baseVertexDraw
    }

    fun baseVertex(baseVertex: IntBuffer): MultiDrawBaseVertex {
        baseVertexMultiDraw.baseVertex = baseVertex
        return baseVertexMultiDraw
    }

    fun range(start: Int, end: Int): DrawRange {
        drawRange.start = start
        drawRange.end = end
        return drawRange
    }

}

class DrawRange(private val glDrawCommands: GLDrawerLoader) : ElementsDrawCommand {
    internal var start: Int = 0
    internal var end: Int = 1
    private val drawRangeBaseVertex = DrawRangeBaseVertex(glDrawCommands, this)

    override fun elements(model: Model) {
        glDrawCommands.drawRangeElements(model, start, end)
    }

    fun baseVertex(baseVertex: Int): ElementsDrawCommand {
        drawRangeBaseVertex.baseVertex = baseVertex
        return drawRangeBaseVertex
    }
}