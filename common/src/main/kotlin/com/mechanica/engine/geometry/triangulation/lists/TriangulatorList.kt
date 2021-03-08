package com.mechanica.engine.geometry.triangulation.lists

import com.cave.library.vector.vec2.Vector2
import com.cave.library.vector.vec2.vec
import com.mechanica.engine.util.extensions.fori
import com.mechanica.engine.util.extensions.foriIndexed
import com.mechanica.engine.util.extensions.loopedGet

class TriangulatorList<V : Vector2>() {
    private val vertices = ArrayList<NodeImpl>()

    private var _head: NodeImpl? = null
    val head: Node?
        get() = _head ?: initializeHead()

    constructor(path: Array<V>) : this() {
        addAll(path)
    }

    constructor(path: List<V>) : this() {
        addAll(path)
    }

    fun add(point: V): Node {
        return add(vertices.size, point)
    }

    fun add(index: Int, point: V): Node {
        val node = NodeImpl(point, index)
        vertices.add(index, node)

        syncIndices()
        node.reset()
        node.relink()
        return node
    }

    fun addAfter(node: Node, point: V): Node {
        val after = node as? NodeImpl
        return if (after != null) {
            add(after.index, point)
        } else node
    }

    fun addAll(path: Array<V>) {
        path.fori(this::add)
    }

    fun addAll(path: List<V>) {
        path.fori(this::add)
    }

    fun relinkAll() {
        _head = vertices.firstOrNull()
        vertices.foriIndexed{ n, i ->
            n.index = i
            n.reset()
        }
    }

    fun vertices(): List<Vector2> {
        return this.vertices.map { vec(it.point) }
    }

    inline fun loopNodes(action: (Node) -> Unit) {
        val cursor = this.head

        if (cursor != null) {
            loopFrom(cursor, action)
        }
    }

    inline fun loopFrom(node: Node, action: (Node) -> Unit) {
        var cursor = node

        action(cursor)

        while (cursor.next !== node) {
            cursor = cursor.next
            action(cursor)
        }
    }

    private fun initializeHead(): NodeImpl? {
        _head = vertices.firstOrNull()
        return _head
    }

    private fun syncIndices() {
        vertices.foriIndexed { n, i ->
            n.index = i
        }
    }

    abstract inner class Node : Vector2 {
        abstract val point: V
        abstract val index: Int

        abstract val prev: Node
        abstract val next: Node

        abstract fun unlink()
        abstract fun relink()
        abstract fun linkTo(other: Node): Boolean
    }

    private inner class NodeImpl(override val point: V, override var index: Int) : Vector2 by point, Node() {
        override lateinit var prev: NodeImpl
            private set
        override lateinit var next: NodeImpl
            private set

        fun reset() {
            prev = vertices.loopedGet(index - 1)
            next = vertices.loopedGet(index + 1)
        }

        override fun unlink() {
            if (this === _head) {
                _head = next
            }
            next.prev = prev
            prev.next = next
        }

        override fun relink() {
            if (this === vertices.firstOrNull()) {
                _head = this
            }
            next.prev = this
            prev.next = this
        }

        override fun linkTo(other: Node): Boolean {
            if (other is NodeImpl) {
                this.next = other
                other.prev = this
                return true
            }
            return false
        }
    }

}