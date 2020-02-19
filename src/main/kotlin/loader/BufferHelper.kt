@file:Suppress("unused") // There will be many functions here that go unused most of the time

package loader

import org.lwjgl.system.MemoryUtil.*
import java.nio.Buffer
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.nio.ShortBuffer
import java.util.*

/**
 * Created by domin on 27/10/2017.
 */

val buffers = ArrayList<Buffer>()

private fun scheduleMemFree(buffer: Buffer){
    buffers.add(buffer)
}

fun freeMemory(){
    buffers.forEach { memFree(it) }
    buffers.clear()
}

inline fun <reified T> getBuffer(size: Int): Buffer where T : Number{
    return when (T::class.simpleName){
        "Int" -> {
            val buffer = memAllocInt(size)
            buffers.add(buffer)
            buffer}
        "Float"  -> {
            val buffer = memAllocFloat(size)
            buffers.add(buffer)
            buffer}
        "Double" -> {
            val buffer = memAllocDouble(size)
            buffers.add(buffer)
            buffer}
        "Long" -> {
            val buffer = memAllocLong(size)
            buffers.add(buffer)
            buffer}
        "Short" -> {
            val buffer = memAllocShort(size)
            buffers.add(buffer)
            buffer}
        else -> {
            val buffer = memAlloc(size)
            buffers.add(buffer)
            buffer}
    }
}

inline fun <reified T> getBuffer(data: Array<T>): Buffer where T : Number{
    return when (data[0]){
        is Int -> {
            val buffer = memAllocInt(data.size)
            data.forEach { buffer.put(it as Int) }
            buffer.flip()
            buffers.add(buffer)
            buffer}
        is Float  -> {
            val buffer = memAllocFloat(data.size)
            data.forEach { buffer.put(it as Float) }
            buffer.flip()
            buffers.add(buffer)
            buffer}
        is Double -> {
            val buffer = memAllocDouble(data.size)
            data.forEach { buffer.put(it as Double) }
            buffer.flip()
            buffers.add(buffer)
            buffer}
        is Long -> {
            val buffer = memAllocLong(data.size)
            data.forEach { buffer.put(it as Long) }
            buffer.flip()
            buffers.add(buffer)
            buffer}
        is Short -> {
            val buffer = memAllocShort(data.size)
            data.forEach { buffer.put(it as Short) }
            buffer.flip()
            buffers.add(buffer)
            buffer}
        else -> {
            val buffer = memAlloc(data.size)
            data.forEach { buffer.put(it as Byte) }
            buffer.flip()
            buffers.add(buffer)
            buffer}
    }
}

fun ShortArray.toBuffer(): ShortBuffer{
    val buffer = memAllocShort(this.size)
    this.forEach { buffer.put(it) }
    buffer.flip()
    buffers.add(buffer)
    return buffer
}


fun IntArray.toBuffer(): IntBuffer {
    val buffer = memAllocInt(this.size)
    this.forEach { buffer.put(it) }
    buffer.flip()
    buffers.add(buffer)
    return buffer
}


fun FloatArray.toBuffer(): FloatBuffer {
    val buffer = memAllocFloat(this.size)
    this.forEach { buffer.put(it) }
    buffer.flip()
    buffers.add(buffer)
    return buffer
}

fun emptyFloatBuffer(size: Int): FloatBuffer {
    val buffer = memAllocFloat(size)
    buffers.add(buffer)
    return buffer
}

fun emptyShortBuffer(size: Int): FloatBuffer {
    val buffer = memAllocFloat(size)
    buffers.add(buffer)
    return buffer
}

fun ShortBuffer.contentsToString(): String {
    val sb = StringBuilder()
    while (this.hasRemaining()) {
        sb.append(this.get()).append(", ")
    }
    return sb.toString()
}

fun FloatBuffer.contentsToString(): String {
    val sb = StringBuilder()
    while (this.hasRemaining()) {
        sb.append(this.get()).append(", ")
    }
    return sb.toString()
}