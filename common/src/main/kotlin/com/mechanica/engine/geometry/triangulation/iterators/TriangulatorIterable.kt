package com.mechanica.engine.geometry.triangulation.iterators

import com.mechanica.engine.geometry.triangulation.triangulators.GrahamScanTriangulator

interface TriangulatorIterable : Iterable<GrahamScanTriangulator.Node> {

    val head: GrahamScanTriangulator.Node?

    fun rewind()

    fun removeLink(node: GrahamScanTriangulator.Node)
}