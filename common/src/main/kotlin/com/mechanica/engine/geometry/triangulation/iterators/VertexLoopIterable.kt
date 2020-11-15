package com.mechanica.engine.geometry.triangulation.iterators

import com.mechanica.engine.geometry.triangulation.triangulators.GrahamScanTriangulator

class VertexLoopIterable(private val list: ArrayList<GrahamScanTriangulator.Node>) : TriangulatorIterable {
    override val head: GrahamScanTriangulator.Node
        get() = list.first()

    private val iterator = VertexLoopIterator()

    init {
        rewind()
    }

    override fun rewind() {
        for (n in list) {
            n.rewind()
        }
    }

    override fun removeLink(node: GrahamScanTriangulator.Node) {
        node.next.prev = node.prev
        node.prev.next = node.next
    }

    override fun iterator(): Iterator<GrahamScanTriangulator.Node> {
        iterator.lastIndex = -1
        iterator.current = head
        return iterator
    }

    private inner class VertexLoopIterator: Iterator<GrahamScanTriangulator.Node> {
        var current = head
        var lastIndex = -1

        override fun hasNext() = current.index > lastIndex

        override fun next(): GrahamScanTriangulator.Node {
            val cursor = current
            lastIndex = current.index
            current = current.next
            return cursor
        }
    }
}