package com.mechanica.engine.geometry.triangulation.iterators

import com.mechanica.engine.geometry.triangulation.Triangulator

class ConcaveVertexIterable(private val list: ArrayList<Triangulator.Node>) : TriangulatorIterable {
    override var head: Triangulator.Node? = null

    private val iterator = ConcaveVertexIterator()

    init {
        rewind()
    }

    fun setNewHead(head: Triangulator.Node?) {
        this.head = head
        iterator.current = head
    }

    override fun rewind() {
        var current: Triangulator.Node? = null
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

    override fun removeLink(node: Triangulator.Node) {
        node.prevConcave?.nextConcave = node.nextConcave
        node.nextConcave?.prevConcave = node.prevConcave
    }

    override fun iterator(): Iterator<Triangulator.Node> {
        iterator.current = head
        return iterator
    }

    private inner class ConcaveVertexIterator : Iterator<Triangulator.Node> {
        var current = head

        override fun hasNext(): Boolean = current != null

        override fun next(): Triangulator.Node {
            val cursor = current
            current = current?.nextConcave
            return cursor ?: throw IllegalStateException("Cannot iterate over null values")
        }
    }
}