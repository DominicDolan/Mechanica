package com.mechanica.engine.drawer.superclass.path

import com.mechanica.engine.drawer.shader.PathRenderer
import com.mechanica.engine.drawer.state.DrawState
import com.mechanica.engine.unit.vector.DynamicVector
import com.mechanica.engine.unit.vector.Vector
import com.mechanica.engine.unit.vector.VectorArray

class PathDrawerImpl(private val state: DrawState): PathDrawer {

    private val renderer = PathRenderer()
    private val line = ArrayList<DynamicVector>()

    init {
        line.add(DynamicVector.create())
        line.add(DynamicVector.create())
    }

    override fun path(path: Array<out Vector>, count: Int) {
        renderer.fillFloats(path, count)
        draw()
    }

    override fun path(path: VectorArray, count: Int) {
        renderer.fillFloats(path, count)
        draw()
    }

    override fun path(path: List<Vector>, count: Int) {
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
        state.reset()
    }

    private fun draw() {
        val matrix = state.transformation.getTransformationMatrix()
        renderer.color = state.color.fill
        renderer.stroke = state.shader.strokeWidth.value.toFloat()
        renderer.render(matrix)
    }
}