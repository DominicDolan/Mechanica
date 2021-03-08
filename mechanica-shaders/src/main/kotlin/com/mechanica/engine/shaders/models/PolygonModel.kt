package com.mechanica.engine.shaders.models

import com.cave.library.vector.vec2.Vector2
import com.mechanica.engine.geometry.triangulation.Triangulator
import com.mechanica.engine.geometry.triangulation.triangulators.GrahamScanTriangulator
import com.mechanica.engine.shaders.attributes.AttributeArray
import com.mechanica.engine.shaders.utils.ElementIndexArray

open class PolygonModel(vertices: Array<out Vector2>,
                        triangulator: Triangulator = GrahamScanTriangulator(vertices))
: Model(
        AttributeArray.createPositionArray(vertices),
        ElementIndexArray(triangulator.triangulate()),
    )
{

    constructor(vertices: List<Vector2>) : this(vertices.toTypedArray())

    init {
        vertexCount = triangulator.indexCount
    }

}