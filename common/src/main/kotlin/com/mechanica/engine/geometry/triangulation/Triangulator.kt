package com.mechanica.engine.geometry.triangulation

import com.mechanica.engine.geometry.triangulation.iterators.TriangulatorIterable
import com.mechanica.engine.unit.vector.LightweightVector
import com.mechanica.engine.unit.vector.Vector
import com.mechanica.engine.util.extensions.indexLooped

abstract class Triangulator(path: Array<LightweightVector>) {

    val ccw: Boolean
    val vertices = ArrayList<Node>()

    abstract val indices: ShortArray
    abstract var indexCount: Int
    abstract val concaveVertices: TriangulatorIterable

    abstract fun triangulate(): ShortArray
    abstract fun rewind()

    init {
        if (path.size < 3) throw IllegalStateException("Can't triangulate less than 3 vertices")
        Node(path[0])

        ccw = addAllFromPath(path)
    }

    fun add(vector: LightweightVector): Node {
        val n = Node(vector)
        rewind()
        return n
    }

    fun add(index: Int, vector: LightweightVector): Node {
        val n = Node(vector, index)
        rewind()
        return n
    }


    private fun addAllFromPath(path: Array<LightweightVector>):  Boolean {
        var totalArea = 0.0
        for (i in 1 until path.size) {
            Node(path[i])

            if (i > 0) totalArea += calculateLineArea(path[i - 1], path[i])
        }
        return totalArea < 0.0
    }


    inner class Node(vertex: Vector, index: Int = vertices.size): Vector {
        override var x: Double = vertex.x
        override var y: Double = vertex.y

        private val defaultPrevious: Node
            get() = vertices[vertices.indexLooped(listIndex - 1)]
        private val defaultNext: Node
            get() = vertices[vertices.indexLooped(listIndex + 1)]

        var prev: Node
        var next: Node
        var nextConcave: Node? = null
        var prevConcave: Node? = null

        val isEar: Boolean
            get() = isEar(concaveVertices)
        val isConcave: Boolean
            get() = isConcave(prev, this, next, ccw)

        private var _listIndex = index
        val listIndex: Int
            get() {
                if (_listIndex == -1 || vertices[_listIndex] !== this) {
                    _listIndex = vertices.indexOf(this)
                }
                return _listIndex
            }

        init {
            vertices.add(index, this)
            prev = defaultPrevious
            next = defaultNext
        }

        fun rewind() {
            prev = defaultPrevious
            next = defaultNext
        }

        override fun toString(): String {
            return "($x, $y)"
        }
    }
}