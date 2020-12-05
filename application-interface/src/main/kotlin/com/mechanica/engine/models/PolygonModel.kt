package com.mechanica.engine.models

import com.mechanica.engine.geometry.triangulation.Triangulator
import com.mechanica.engine.geometry.triangulation.triangulators.GrahamScanTriangulator
import com.mechanica.engine.shader.attributes.AttributeArray
import com.mechanica.engine.unit.vector.Vector
import com.mechanica.engine.vertices.IndexArray

open class PolygonModel(vertices: Array<out Vector>,
                        triangulator: Triangulator = GrahamScanTriangulator(vertices))
: Model(
        AttributeArray.createPositionArray(vertices),
        IndexArray.create(*triangulator.triangulate()),
    )
{

    constructor(vertices: List<Vector>) : this(vertices.toTypedArray())

    init {
        vertexCount = triangulator.indexCount
    }

}