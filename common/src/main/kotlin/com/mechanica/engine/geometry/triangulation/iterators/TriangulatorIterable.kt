package com.mechanica.engine.geometry.triangulation.iterators

import com.mechanica.engine.geometry.triangulation.Triangulator

interface TriangulatorIterable : Iterable<Triangulator.Node> {

    val head: Triangulator.Node?

    fun rewind()

    fun removeLink(node: Triangulator.Node)
}