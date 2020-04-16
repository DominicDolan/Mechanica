package geometry.triangulation.iterators

import geometry.triangulation.TriangulatorList

interface TriangulatorIterable : Iterable<TriangulatorList.Node> {

    fun setNewHead(head: TriangulatorList.Node?)
}