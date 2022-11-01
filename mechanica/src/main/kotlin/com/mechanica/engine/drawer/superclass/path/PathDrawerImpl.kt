package com.mechanica.engine.drawer.superclass.path

import com.cave.library.vector.vec2.MutableVector2
import com.cave.library.vector.vec2.Vector2
import com.mechanica.engine.drawer.shader.PathRenderer
import com.mechanica.engine.drawer.state.DrawState
import com.mechanica.engine.util.VectorArray

class PathDrawerImpl(private val state: DrawState): PathDrawer {

    private val renderer = PathRenderer()
    private val line = ArrayList<MutableVector2>()

    init {
        line.add(MutableVector2.create())
        line.add(MutableVector2.create())
    }

    override fun path(path: Array<out Vector2>, count: Int) {
        renderer.fillFloats(path, count)
        draw()
    }

    override fun path(path: VectorArray, count: Int) {
        renderer.fillFloats(path, count)
        draw()
    }

    override fun path(path: List<Vector2>, count: Int) {
        renderer.fillFloats(path, count)
        draw()
    }

    override fun line(x1: Number, y1: Number, x2: Number, y2: Number) {
        line[0].x = x1.toDouble()
        line[0].y = y1.toDouble()
        line[1].x = x2.toDouble()
        line[1].y = y2.toDouble()
        renderer.fillFloats(line, 2)
        draw()
    }

    private fun draw() {
        val matrix = state.transformation.getTransformationMatrix()
        renderer.color = state.color.fill
        renderer.stroke = state.shader.strokeWidth.value.toFloat()
        renderer.render(matrix)
        state.reset()
    }
}