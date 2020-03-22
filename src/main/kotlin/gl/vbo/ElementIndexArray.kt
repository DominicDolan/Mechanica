package gl.vbo

import gl.vbo.pointer.VBOPointer
import org.lwjgl.opengl.GL20.*

class ElementIndexArray(indices: ShortArray) : VBO<ShortArray>(indices, VBOPointer.elementArrayPointer) {
    constructor(vertexCount: Int) : this(ShortArray(vertexCount))

    override fun getArraySize(array: ShortArray)= array.size

    override fun bufferSubData(target: Int, offset: Long, array: ShortArray) {
        glBufferSubData(target, offset, array)
    }

    override fun initBufferData(target: Int, array: ShortArray) {
        glBufferData(target, array, GL_STATIC_DRAW)
    }

    override fun initBufferData(target: Int, arraySize: Int) {
        initBufferData(target, ShortArray(arraySize))
    }
}