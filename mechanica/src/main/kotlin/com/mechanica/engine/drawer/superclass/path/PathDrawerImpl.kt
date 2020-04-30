package com.mechanica.engine.drawer.superclass.path

import com.mechanica.engine.drawer.DrawData
import com.mechanica.engine.unit.vector.DynamicVector
import com.mechanica.engine.unit.vector.Vector
import com.mechanica.engine.drawer.shader.PathRenderer
import com.mechanica.engine.unit.vector.VectorArray
import org.joml.Matrix4f

class PathDrawerImpl(private val data: DrawData): PathDrawer {

    private val renderer = PathRenderer()
    private val transformation = Matrix4f().identity()
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
        data.rewind()
    }

    private fun draw() {
        data.getTransformationMatrix(transformation)
        renderer.color = data.strokeColor
        renderer.stroke = data.strokeWidth.toFloat()
        renderer.render(transformation)
    }
}