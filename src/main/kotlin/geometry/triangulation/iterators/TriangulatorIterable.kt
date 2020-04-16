package geometry.triangulation.iterators

import geometry.triangulation.Triangulator

interface TriangulatorIterable : Iterable<Triangulator.Node> {

    val head: Triangulator.Node?

    fun rewind()

    fun removeLink(node: Triangulator.Node)
}