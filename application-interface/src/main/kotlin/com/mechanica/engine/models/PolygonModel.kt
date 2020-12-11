package com.mechanica.engine.models

import com.mechanica.engine.geometry.triangulation.Triangulator
import com.mechanica.engine.geometry.triangulation.triangulators.GrahamScanTriangulator
import com.mechanica.engine.shader.attributes.AttributeArray
import com.mechanica.engine.unit.vector.Vector
import com.mechanica.engine.utils.ElementIndexArray

open class PolygonModel(vertices: Array<out Vector>,
                        triangulator: Triangulator = GrahamScanTriangulator(vertices))
: Model(
        AttributeArray.createPositionArray(vertices),
        ElementIndexArray(triangulator.triangulate()),
    )
{

    constructor(vertices: List<Vector>) : this(vertices.toTypedArray())

    init {
        vertexCount = triangulator.indexCount
    }

}