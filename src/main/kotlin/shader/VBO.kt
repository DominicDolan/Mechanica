package shader

import loader.toBuffer
import org.lwjgl.opengl.GL20
import util.units.Vector

class VBO private constructor(val id: Int, val vertexCount: Int, val attributePointer: AttributePointer) {
    fun bind() {
        GL20.glBindBuffer(GL20.GL_ARRAY_BUFFER, id)
        attributePointer.enable()
    }

    companion object {
        fun create(vertices: Array<Vector>, attributePointer: AttributePointer): VBO {
            val cs = attributePointer.coordinateSize
            val floats = FloatArray(vertices.size*cs)
            for (i in vertices.indices) {
                val floatsIndex = i*cs
                floats[floatsIndex] = vertices[i].x.toFloat()
                floats[floatsIndex+1] = vertices[i].y.toFloat()
                for (j in 2 until cs) {
                    floats[floatsIndex + j] = 1f
                }
            }
            return genBuffer(floats, vertices.size, attributePointer)
        }

        fun genBuffer(floats: FloatArray, vertexCount: Int, attributePointer: AttributePointer): VBO {
            val id = GL20.glGenBuffers()
            GL20.glBindBuffer(GL20.GL_ARRAY_BUFFER, id)
            GL20.glBufferData(GL20.GL_ARRAY_BUFFER, floats.toBuffer(), GL20.GL_STATIC_DRAW)
            return VBO(id, vertexCount, attributePointer)
        }
    }
}

