@file:Suppress("unused") // There will be many functions here that go unused most of the time
package loader

import animation.FrameAnimation
import com.vividsolutions.jts.geom.Coordinate
import com.vividsolutions.jts.geom.GeometryFactory
import font.FontType
import gl.utils.IndexedVertices
import gl.utils.loadImage
import graphics.Image
import models.Model
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.*
import org.lwjgl.opengl.GL11.*
import org.lwjgl.stb.STBImage.*
import org.lwjgl.system.MemoryUtil.*
import resources.Res
import resources.Resource
import resources.ResourceDirectory
import util.extensions.toFloatArray
import util.triangulate.EarClipper
import util.units.Vector
import java.io.*
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.nio.ShortBuffer
import java.nio.channels.FileChannel
import kotlin.collections.ArrayList
import kotlin.system.exitProcess
import java.io.File
import kotlin.collections.HashMap


/**
 * Created by domin on 26/10/2017.
 */

private val vaos = ArrayList<Int>()
private val vbos = ArrayList<Int>()

fun loadTriangulatedArrays(positions: List<Vector>) = loadTriangulatedArrays(positions.toFloatArray())

fun loadTriangulatedArrays(positions: FloatArray): IndexedVertices {
    val coords = ArrayList<Coordinate>()
    val coordsArray = arrayOfNulls<Coordinate>(positions.size / 3 + 1)
    run {
        var i = 0
        while (i < positions.size) {
            coords.add(Coordinate(positions[i].toDouble(), positions[i + 1].toDouble(), positions[i + 2].toDouble()))
            coordsArray[i / 3] = Coordinate(positions[i].toDouble(), positions[i + 1].toDouble(), positions[i + 2].toDouble())
            i += 3
        }
    }

    coordsArray[positions.size / 3] = Coordinate(positions[0].toDouble(), positions[1].toDouble(), positions[2].toDouble())
    val geomFact = GeometryFactory()
    val polygon = geomFact.createPolygon(coordsArray)

    val clipper = EarClipper(polygon)
    val triangulation = clipper.result

    val newPositionsList = ArrayList<Float>()
    val indicesList = ArrayList<Short>()

    val vertexGetter = HashMap<String, Short>()

    var index: Short = 0
    var len = triangulation.numGeometries
    for (i in 0 until len) {
        val triangle = triangulation.getGeometryN(i).coordinates.clone()

        for (j in 0..2) {
            val coord = triangle[j]

            val key = hashCoordinates(coord)
            if (vertexGetter.containsKey(key)) {
                val existingIndex = vertexGetter[key]
                indicesList.add(existingIndex!!)
            } else {
                indicesList.add(index)
                vertexGetter[key] = index
                index++

                newPositionsList.add(coord.x.toFloat())
                newPositionsList.add(coord.y.toFloat())
                newPositionsList.add(0f)
            }
        }
    }

    len = newPositionsList.size
    val newPositions = FloatArray(len)
    for (i in 0 until len) {
        newPositions[i] = newPositionsList[i]
    }

    len = indicesList.size
    val indices = ShortArray(len)
    for (i in 0 until len) {
        indices[i] = indicesList[i]
    }

    return IndexedVertices(newPositions, indices)
}

private fun hashCoordinates(coordinate: Coordinate): String {
    return hashCoordinates(coordinate.x, coordinate.y)
}

private fun hashCoordinates(x: Double, y: Double): String {
    return "$x:$y"
}

private fun defaultGenerateTexture(details: ImageDetails) {
    glBindTexture(GL_TEXTURE_2D, details.id)
//    glGenerateMipmap(GL_TEXTURE_2D)
//    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR)
//    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
//    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, details.width, details.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, details.data)
}

private data class ImageDetails(val data: ByteBuffer?, val id: Int, val width: Int, val height: Int, val components: Int)

fun loadTextureDirectory(directory: String) = ResourceDirectory(directory).map { loadImage(it) }.toList()
fun loadTextureDirectory(directory: ResourceDirectory) = directory.map { loadImage(it) }.toList()

fun loadAnimation(directory: ResourceDirectory, frameRate: Double = 24.0) = FrameAnimation(loadTextureDirectory(directory), frameRate)

@Throws(IOException::class)
fun ioResourceToByteBuffer(resource: String, bufferSize: Int): ByteBuffer {
    val buffer: ByteBuffer
    val file = File(resource)
    if (file.isFile) {
        val fis = FileInputStream(file)
        val fc = fis.channel
        buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size())
        fc.close()
        fis.close()
    } else {
        System.err.println("Something went wrong")
        buffer = BufferUtils.createByteBuffer(bufferSize)
    }
//    else {
//        println("Is not file")
//        buffer = BufferUtils.createByteBuffer(bufferSize)
//        val source = url.openStream() ?: throw FileNotFoundException(resource)
//        source.use { s ->
//            Channels.newChannel(s).use { rbc ->
//                while (true) {
//                    val bytes = rbc.read(buffer)
//                    if (bytes == -1)
//                        break
//                    if (buffer.remaining() == 0)
//                        buffer = resizeBuffer(buffer, buffer.capacity() * 2)
//                }
//                buffer.flip()
//            }
//        }
//    }
    return buffer
}

fun ioResourceToByteBuffer(resource: InputStream): ByteBuffer {
    val bytes = resource.readAllBytes()
    val buffer = BufferUtils.createByteBuffer(bytes.size)
    buffer.put(bytes)
    buffer.flip()
    return buffer
}

fun ioResourceToByteBuffer(file: File, bufferSize: Int): ByteBuffer {
    val buffer: ByteBuffer
    if (file.isFile) {
        val fis = FileInputStream(file)
        val fc = fis.channel
        buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size())
        fc.close()
        fis.close()
    } else {
        System.err.println("Didn't work correctly")
        buffer = BufferUtils.createByteBuffer(bufferSize)
    }
    return buffer
}

fun loadTextFile(filename: String): String {
    val text = StringBuilder()
    try {
        val reader = BufferedReader(FileReader(filename))
        var line = reader.readLine()
        do {
            text.append(line)
            line = reader.readLine()
        } while (line != null)
        reader.close()
    } catch (e: IOException) {
        e.printStackTrace()
        exitProcess(-1)
    }

    return text.toString()
}

fun loadFont(name: String): FontType {
    return FontType(loadImage(Res.font["$name.png"]), Res.font["$name.fnt"])
}

fun loadBufferedReader(file: String): BufferedReader? {
    return try {
        BufferedReader(FileReader(file))
    } catch (e: Exception) {
        e.printStackTrace()
        System.err.println("Couldn't read font meta file!")
        null
    }

}

private fun resizeBuffer(buffer: ByteBuffer, newCapacity: Int): ByteBuffer {
    val newBuffer = BufferUtils.createByteBuffer(newCapacity)
    buffer.flip()
    newBuffer.put(buffer)
    return newBuffer
}

private fun bindIndicesBuffer(indices: ShortArray) {
    val vboID = GL15.glGenBuffers()
    vbos.add(vboID)
    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID)
    val buffer = storeDataInShortBuffer(indices)
    GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW)
    memFree(buffer)
}

private fun bindIndicesBuffer(indices: ShortBuffer) {
    val vboID = GL15.glGenBuffers()
    vbos.add(vboID)
    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID)
    GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW)
    freeMemory()
}


private fun storeDataInAttributeList(attributeNumber: Int, data: FloatArray, coordinateSize: Int = 3 ) {
    val vboID = GL15.glGenBuffers()
    vbos.add(vboID)
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID)
    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data.toBuffer(), GL15.GL_STATIC_DRAW)
    freeMemory()
    GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL_FLOAT, false, 0, 0)
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
}

private fun storeDataInAttributeList(attributeNumber: Int, data: FloatBuffer, coordinateSize: Int = 3 ) {
    val vboID = GL15.glGenBuffers()
    vbos.add(vboID)
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID)
    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_STATIC_DRAW)
    freeMemory()
    GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL_FLOAT, false, 0, 0)
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
}

private fun storeDataInAttributeList(attributeNumber: Int, coordinateSize: Int, data: FloatBuffer) {
    val vboID = GL15.glGenBuffers()
    vbos.add(vboID)
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID)
    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_STATIC_DRAW)
    GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL_FLOAT, false, 0, 0)
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
}

private fun storeDataInIntBuffer(data: IntArray): IntBuffer {
    val buffer = memAllocInt(data.size)
    buffer.put(data)
    buffer.flip()
    return buffer
}


fun storeDataInShortBuffer(data: ShortArray): ShortBuffer {

    val buffer = getBuffer(data.toTypedArray()) as ShortBuffer
    buffer.put(data)
    buffer.flip()
    return buffer
}


fun storeDataInFloatBuffer(data: FloatArray): FloatBuffer {
    val buffer = memAllocFloat(data.size)
    buffer.put(data)
    buffer.flip()
    return buffer
}