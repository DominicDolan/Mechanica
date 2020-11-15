package com.mechanica.engine.geometry.triangulation

import com.mechanica.engine.geometry.rectangleArea
import com.mechanica.engine.unit.vector.InlineVector
import com.mechanica.engine.unit.vector.Vector
import com.mechanica.engine.unit.vector.vec
import com.mechanica.engine.util.extensions.foriIndexed
import com.mechanica.engine.util.extensions.indexLooped


interface Triangulator {
    val indices: ShortArray
    val indexCount: Int

    fun triangulate(): ShortArray

}

abstract class AbstractTriangulator<N : TriangulatorNode>(path: Array<out Vector>) : Triangulator {

    private var hasChanged: Boolean = true

    private var _ccw: Boolean = false
    val ccw: Boolean
        get() {
            if (hasChanged) reorganise()
            return _ccw
        }

    private var _area: Double = 0.0
    val area: Double
        get() {
            if (hasChanged) reorganise()
            return _area
        }
    val vertices = ArrayList<N>()

    init {
        if (path.size < 3) throw IllegalStateException("Can't triangulate less than 3 vertices")
        addAll(path)
    }

    abstract fun createNode(vector: InlineVector): N

    fun add(vector: InlineVector): N {
        val n = createNode(vector)
        vertices.add(n)
        reorganise()
        return n
    }

    fun add(index: Int, vector: InlineVector): N {
        val n = createNode(vector)
        vertices.add(index, n)
        reorganise()
        return n
    }

    fun addAll(path: Array<out Vector>) {
        for (element in path) {
            val n = createNode(vec(element))
            vertices.add(n)
        }
        reorganise()
    }

    protected val N.prev: N
        get() = vertices[vertices.indexLooped(correctIndex() - 1)]
    protected val N.next: N
        get() = vertices[vertices.indexLooped(correctIndex() + 1)]

    protected val N.isConcave: Boolean
        get() {
            val area = rectangleArea(next, prev, this)
            val isLeft = area > 0.0
            return (isLeft && !ccw) || (!isLeft && ccw)
        }

    protected open fun reorganise() {
        _area = calculatePolygonArea()
        _ccw = _area < 0.0

        vertices.foriIndexed { n, i ->
            n.index = i
        }
        hasChanged = false
    }

    private fun calculatePolygonArea(): Double {
        var totalArea = 0.0
        for (i in 1 until vertices.size) {
            if (i > 0) totalArea += calculateLineArea(vec(vertices[i - 1]), vec(vertices[i]))
        }
        return totalArea
    }

    private fun N.correctIndex(): Int {
        if (vertices[index] !== this) index = vertices.indexOf(this)
        return index
    }

}