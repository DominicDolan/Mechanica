package com.mechanica.engine.samples.triangulation

import com.cave.library.vector.vec2.Vector2
import com.mechanica.engine.geometry.shapes.Triangle
import com.mechanica.engine.shaders.attributes.AttributeArray
import com.mechanica.engine.shaders.models.Model

/**
 * A [Model] over a fixed-capacity vertex buffer that can be refilled every frame.
 *
 * Rebuilding a `PolygonModel` each frame would allocate a GL buffer each frame and never free one,
 * which matters here because dragging a vertex rebuilds the geometry on every frame it moves.
 * This uploads into the same buffer instead.
 */
class TriangleBatch(triangleCapacity: Int) {

    private val positions =
        AttributeArray.createPositionArray(Array<Vector2>(triangleCapacity * 3) { Vector2.create(0.0, 0.0) })

    val model = Model(positions)

    fun fill(triangles: List<Triangle>) {
        val vertices = positions.value
        var written = 0
        for (triangle in triangles) {
            if (written + 3 > vertices.size) break
            vertices[written++].set(triangle.a.x, triangle.a.y)
            vertices[written++].set(triangle.b.x, triangle.b.y)
            vertices[written++].set(triangle.c.x, triangle.c.y)
        }
        model.vertexCount = written
        positions.updateBuffer()
    }
}
