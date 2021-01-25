package com.mechanica.engine.shaders.models

import com.mechanica.engine.geometry.triangulation.Triangulator
import com.mechanica.engine.geometry.triangulation.triangulators.GrahamScanTriangulator
import com.mechanica.engine.shaders.attributes.AttributeArray
import com.mechanica.engine.shaders.utils.ElementIndexArray
import com.mechanica.engine.unit.vector.Vector

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