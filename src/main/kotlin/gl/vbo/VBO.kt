package gl.vbo

import gl.vbo.pointer.AttributePointer
import gl.vbo.pointer.VBOPointer
import org.lwjgl.opengl.GL20

@Suppress("LeakingThis") // The State of the VBO is set before any leaking occurs
abstract class VBO<T> protected constructor(array: T, private val pointer: VBOPointer) {
    private var capacity: Int
    val id: Int = GL20.glGenBuffers()
    var vertexCount: Int

    init {
        GL20.glBindBuffer(pointer.bufferType, id)
        vertexCount = getArraySize(array)/pointer.coordinateSize
        capacity = vertexCount
        initBufferData(pointer.bufferType, array)
    }

    fun bind() {
        GL20.glBindBuffer(pointer.bufferType, id)
        if (pointer is AttributePointer) {
            pointer.enable()
        }
    }

    fun update(array: T, from: Int = 0) {
        val cs = pointer.coordinateSize
        val byteOffset = from*cs*pointer.variableSize

        val vertexCount = getArraySize(array)/pointer.coordinateSize
        if (vertexCount + from > capacity) {
            increaseSize()
        }
        this.vertexCount = vertexCount

        GL20.glBindBuffer(pointer.bufferType, id)
        bufferSubData(pointer.bufferType, byteOffset.toLong(), array)
    }

    protected abstract fun getArraySize(array: T): Int

    protected abstract fun bufferSubData(target: Int, offset: Long, array: T)
    protected abstract fun initBufferData(target: Int, array: T)
    protected abstract fun initBufferData(target: Int, arraySize: Int)

    private fun increaseSize() {
        capacity *= 2
        GL20.glBindBuffer(pointer.bufferType, id)
        initBufferData(pointer.bufferType, capacity*pointer.coordinateSize)
    }

}