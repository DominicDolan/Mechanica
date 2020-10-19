package com.mechanica.engine.models

import com.mechanica.engine.geometry.triangulation.GrahamScanTriangulator
import com.mechanica.engine.geometry.triangulation.Triangulator
import com.mechanica.engine.shader.qualifiers.Attribute
import com.mechanica.engine.unit.vector.Vector
import com.mechanica.engine.vertices.AttributeArray
import com.mechanica.engine.vertices.FloatArrayMaker
import com.mechanica.engine.vertices.IndexArray

open class PolygonModel(vertices: Array<out Vector>,
                        positionAttribute: FloatArrayMaker = AttributeArray.createFrom(Attribute(0).vec2()),
                        triangulator: Triangulator = GrahamScanTriangulator(vertices))
: Model(
        positionAttribute.createArray(vertices),
        IndexArray.create(*triangulator.triangulate()),
    )
{

    constructor(vertices: List<Vector>) : this(vertices.toTypedArray())

    init {
        vertexCount = triangulator.indexCount
    }

}