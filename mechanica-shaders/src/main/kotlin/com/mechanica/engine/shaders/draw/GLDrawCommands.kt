package com.mechanica.engine.shaders.draw

import com.mechanica.engine.shaders.context.ShaderFactory
import com.mechanica.engine.shaders.models.Model
import java.nio.IntBuffer

class GLDraw {
    private val glDrawer = ShaderFactory.drawFactories.glTrianglesDrawer

    val drawTriangles by lazy { GLDrawCommands(glDrawer) }
    val drawTriangleFan by lazy { GLDrawCommands(ShaderFactory.drawFactories.glTriangleFanDrawer) }
    val drawTriangleStrip by lazy { GLDrawCommands(ShaderFactory.drawFactories.glTriangleStripDrawer) }
    val drawPoints by lazy { GLDrawCommands(ShaderFactory.drawFactories.glPointDrawer) }
    val drawLines by lazy { GLDrawCommands(ShaderFactory.drawFactories.glLinesDrawer) }
    val drawLineLoop by lazy { GLDrawCommands(ShaderFactory.drawFactories.glLineLoopDrawer) }
    val drawLineStrip by lazy { GLDrawCommands(ShaderFactory.drawFactories.glLineStripDrawer) }

    fun drawModel(model: Model) {
        if (model.hasIndexArray) {
            glDrawer.drawElements(model.vertexCount)
        } else {
            glDrawer.drawArrays(model.vertexCount)
        }
    }

}

class GLDrawCommands(private val glDrawCommands: GLDrawerFactory) : MultiDrawCommands {

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

class DrawRange(private val glDrawCommands: GLDrawerFactory) : ElementsDrawCommand {
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