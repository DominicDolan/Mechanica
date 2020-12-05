package com.mechanica.engine.graphics

import com.mechanica.engine.context.loader.GLDrawerLoader
import com.mechanica.engine.context.loader.MechanicaLoader
import com.mechanica.engine.models.Model
import java.nio.IntBuffer

class GLDraw {
    private val glDrawer = MechanicaLoader.graphicsLoader.glDrawer

    val drawTriangles by lazy { GLDrawCommands(glDrawer) }
    val drawTriangleFan by lazy { GLDrawCommands(MechanicaLoader.graphicsLoader.glTriangleFanDrawer) }
    val drawTriangleStrip by lazy { GLDrawCommands(MechanicaLoader.graphicsLoader.glTriangleStripDrawer) }
    val drawPoints by lazy { GLDrawCommands(MechanicaLoader.graphicsLoader.glPointDrawer) }
    val drawLines by lazy { GLDrawCommands(MechanicaLoader.graphicsLoader.glLinesDrawer) }
    val drawLineLoop by lazy { GLDrawCommands(MechanicaLoader.graphicsLoader.glLineLoopDrawer) }
    val drawLineStrip by lazy { GLDrawCommands(MechanicaLoader.graphicsLoader.glLineStripDrawer) }

    fun drawModel(model: Model) {
        if (model.hasIndexArray) {
            glDrawer.drawElements(model.vertexCount)
        } else {
            glDrawer.drawArrays(model.vertexCount)
        }
    }

}

class GLDrawCommands(private val glDrawCommands: GLDrawerLoader) : MultiDrawCommands {

    private val instancedDraw = DrawInstanced(glDrawCommands)
    private val baseVertexDraw = DrawBaseVertex(glDrawCommands)
    private val baseVertexMultiDraw = MultiDrawBaseVertex(glDrawCommands)
    private val drawRange = DrawRange(glDrawCommands)

    override fun arrays(vertexCount: Int) {
        glDrawCommands.drawArrays(vertexCount)
    }

    override fun arrays(models: Array<Model>) {
        glDrawCommands.multiDrawArrays(models)
    }

    override fun elements(vertexCount: Int) {
        glDrawCommands.drawElements(vertexCount)
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

    override fun elements(vertexCount: Int) {
        glDrawCommands.drawRangeElements(vertexCount, start, end)
    }

    fun baseVertex(baseVertex: Int): ElementsDrawCommand {
        drawRangeBaseVertex.baseVertex = baseVertex
        return drawRangeBaseVertex
    }
}