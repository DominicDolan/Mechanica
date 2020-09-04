package com.mechanica.engine.models

import com.mechanica.engine.context.loader.GLLoader
import com.mechanica.engine.geometry.triangulation.GrahamScanTriangulator
import com.mechanica.engine.geometry.triangulation.Triangulator
import com.mechanica.engine.shader.qualifiers.Attribute
import com.mechanica.engine.unit.vector.Vector
import com.mechanica.engine.vertices.FloatBufferMaker
import com.mechanica.engine.vertices.IndexArray

open class PolygonModel(vertices: Array<out Vector>,
                        positionAttribute: FloatBufferMaker = Attribute(0).vec2(),
                        triangulator: Triangulator = GrahamScanTriangulator(vertices))
: Model(
        positionAttribute.createBuffer(vertices),
        IndexArray.create(*triangulator.triangulate()),
    draw = {
        GLLoader.graphicsLoader.drawElements(it)
    })
{

    constructor(vertices: List<Vector>) : this(vertices.toTypedArray())

    init {
        vertexCount = triangulator.indexCount
    }

}