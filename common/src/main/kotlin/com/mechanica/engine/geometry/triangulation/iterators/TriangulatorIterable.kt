package com.mechanica.engine.geometry.triangulation.iterators

import com.mechanica.engine.geometry.triangulation.polygon.GrahamScanTriangulator

interface TriangulatorIterable : Iterable<GrahamScanTriangulator.Node> {

    val head: GrahamScanTriangulator.Node?

    fun rewind()

    fun removeLink(node: GrahamScanTriangulator.Node)
}