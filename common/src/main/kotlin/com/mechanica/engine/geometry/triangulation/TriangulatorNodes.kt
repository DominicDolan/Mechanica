package com.mechanica.engine.geometry.triangulation

import com.cave.library.vector.vec2.Vector2

abstract class TriangulatorNode : Vector2 {
    var index: Int = 0
}

//class BasicNode(vertex: Vector2, override val triangulator: Triangulator<BasicNode>, index: Int = triangulator.vertices.size): TriangulatorNode {
//    override var x: Double = vertex.x
//    override var y: Double = vertex.y
//
//    private val vertices = triangulator.vertices
//
//    private var _listIndex = index
//    override val listIndex: Int
//        get() {
//            if (_listIndex == -1 || vertices[_listIndex] !== this) {
//                _listIndex = vertices.indexOf(this)
//            }
//            return _listIndex
//        }
//
//
//    override lateinit var prev: TriangulatorNode
//    override lateinit var next: TriangulatorNode
//
//    init {
//        vertices.add(index, this)
//        prev = super.prev
//        next = super.next
//    }
//
//    fun rewind() {
//        prev = super.prev
//        next = super.next
//    }
//
//    override fun toString(): String {
//        return "($x, $y)"
//    }
//}