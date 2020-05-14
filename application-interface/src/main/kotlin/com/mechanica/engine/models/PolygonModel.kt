package com.mechanica.engine.models

import com.mechanica.engine.geometry.triangulation.GrahamScanTriangulator
import com.mechanica.engine.geometry.triangulation.Triangulator
import com.mechanica.engine.context.loader.GLLoader
import com.mechanica.engine.shader.qualifiers.Attribute
import com.mechanica.engine.unit.vector.LightweightVector
import com.mechanica.engine.vertices.ElementArrayBuffer
import com.mechanica.engine.vertices.FloatBufferMaker

open class PolygonModel(vertices: Array<LightweightVector>,
                        positionAttribute: FloatBufferMaker = Attribute(0).vec2(),
                        triangulator: Triangulator = GrahamScanTriangulator(vertices))
: Model(
        positionAttribute.createBuffer(vertices),
        ElementArrayBuffer.create(*triangulator.triangulate()),
    draw = {
        GLLoader.graphicsLoader.drawElements(it)
    })
{

    constructor(vertices: List<LightweightVector>) : this(vertices.toTypedArray())

    init {
        vertexCount = triangulator.indexCount
    }

}