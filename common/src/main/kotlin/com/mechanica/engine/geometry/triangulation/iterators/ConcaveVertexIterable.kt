package com.mechanica.engine.geometry.triangulation.iterators

import com.mechanica.engine.geometry.triangulation.triangulators.GrahamScanTriangulator

class ConcaveVertexIterable(private val list: ArrayList<GrahamScanTriangulator.Node>) : TriangulatorIterable {
    override var head: GrahamScanTriangulator.Node? = null

    private val iterator = ConcaveVertexIterator()

    init {
        rewind()
    }

    fun setNewHead(head: GrahamScanTriangulator.Node?) {
        this.head = head
        iterator.current = head
    }

    override fun rewind() {
        var current: GrahamScanTriangulator.Node? = null
        for (v in list) {
            if (v.isConcave) {
                val n = current
                if (n == null) {
                    setNewHead(v)
                } else {
                    n.nextConcave = v
                    v.prevConcave = n
                }
                current = v
            }
        }
    }

    override fun removeLink(node: GrahamScanTriangulator.Node) {
        node.prevConcave?.nextConcave = node.nextConcave
        node.nextConcave?.prevConcave = node.prevConcave
    }

    override fun iterator(): Iterator<GrahamScanTriangulator.Node> {
        iterator.current = head
        return iterator
    }

    private inner class ConcaveVertexIterator : Iterator<GrahamScanTriangulator.Node> {
        var current = head

        override fun hasNext(): Boolean = current != null

        override fun next(): GrahamScanTriangulator.Node {
            val cursor = current
            current = current?.nextConcave
            return cursor ?: throw IllegalStateException("Cannot iterate over null values")
        }
    }
}