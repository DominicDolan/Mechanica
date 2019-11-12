@file:Suppress("unused") // There will be many functions here that go unused most of the time
package loader

import animation.FrameAnimation
import com.vividsolutions.jts.geom.Coordinate
import com.vividsolutions.jts.geom.GeometryFactory
import font.FontType
import graphics.Image
import models.Model
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.*
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL30.glGenerateMipmap
import org.lwjgl.stb.STBImage.*
import org.lwjgl.system.MemoryUtil.*
import resources.Res
import resources.Resource
import resources.InternalResource
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

fun loadModel(positions: FloatArray, indices: ShortArray): Model {
    val vaoID = createVAO()
    bindIndicesBuffer(indices.toBuffer())
    storeDataInAttributeList(0, positions)
    unbindVAO()

    return Model(vaoID, indices.size, drawType = GL_TRIANGLES)
}


fun loadModel(positions: FloatArray, indices: ShortArray, textureCoords: FloatArray, texture: Image): Model {
    val vaoID = createVAO()
    bindIndicesBuffer(indices.toBuffer())
    storeDataInAttributeList(0, positions)
    storeDataInAttributeList(1, textureCoords, 2)
    unbindVAO()

    return Model(vaoID, indices.size, texture, drawType = GL_TRIANGLES)
}

fun loadTextureModel(positions: FloatBuffer, textureCoords: FloatBuffer, vertexCount: Int, texture: Image): Model {
    val vaoID = createVAO()
    storeDataInAttributeList(0, 2, positions)
    storeDataInAttributeList(1, 2, textureCoords)
    unbindVAO()

    val model = Model(vaoID, vertexCount, texture)
    model.drawType = GL_TRIANGLES
    return model
}

fun loadQuad(left: Float, top: Float, right: Float, bottom: Float): Model {
    val vertices = floatArrayOf(left, top, 0.0f, left, bottom, 0.0f, right, bottom, 0.0f, right, top, 0.0f)

    val indices = shortArrayOf(0, 1, 2, 0, 2, 3) // The order of vertexrendering.

//    val textureCoords = floatArrayOf(0f, 0f, //V0
//            0f, 1f, //V1
//            1f, 1f, //V2
//            1f, 0f  //V3
//    )

    return loadModel(vertices, indices)
}


fun loadTexturedQuad(texture: Image, left: Float, top: Float, right: Float, bottom: Float): Model {
    val vertices = floatArrayOf(left, top, 0.0f, left, bottom, 0.0f, right, bottom, 0.0f, right, top, 0.0f)

    val indices = shortArrayOf(0, 1, 2, 0, 2, 3) // The order of vertexrendering.

    val textureCoords = floatArrayOf(0f, 0f, //V0
            0f, 1f, //V1
            1f, 1f, //V2
            1f, 0f  //V3
    )

    return loadModel(vertices, indices, textureCoords, texture)
}

fun loadUnitQuad(): Model {
    val vertices = floatArrayOf(0f, 1f, 0.0f, 0f, 0f, 0.0f, 1f, 0f, 0.0f, 1f, 1f, 0.0f)

    val indices = shortArrayOf(0, 1, 2, 0, 2, 3) // The order of vertexrendering.

    val textureCoords = floatArrayOf(0f, 0f, //V0
            0f, 1f, //V1
            1f, 1f, //V2
            1f, 0f  //V3
    )

    return loadModel(vertices, indices, textureCoords, Image(0))
}

fun loadTriangulatedModel(positions: List<Vector>) = loadTriangulatedModel(positions.toFloatArray())

fun loadTriangulatedModel(positions: FloatArray): Model {
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

    return loadModel(newPositions, indices)
}

private fun hashCoordinates(coordinate: Coordinate): String {
    return hashCoordinates(coordinate.x, coordinate.y)
}

private fun hashCoordinates(x: Double, y: Double): String {
    return "$x:$y"
}

fun loadImageFromResource(resource: Resource): Image {
    return loadImageFromMemory(resource.buffer, ::defaultGenerateTexture)
}

internal fun loadInternalTexture(name: String): Image {
    val byteBuffer = InternalResource(name).buffer
    return loadImageFromMemory(byteBuffer, ::defaultGenerateTexture)
}

fun loadTexture(name: String): Image{
    val byteBuffer = ioResourceToByteBuffer(name, 1024)
    return loadImageFromMemory(byteBuffer, ::defaultGenerateTexture)
}

private fun loadImageFromMemory(buffer: ByteBuffer, generateTexture: (ImageDetails) -> Unit): Image {
    val width = getBuffer<Int>(1) as IntBuffer
    val height = getBuffer<Int>(1) as IntBuffer
    val components = getBuffer<Int>(1) as IntBuffer

    val data = stbi_load_from_memory(buffer, width, height, components, 4)
    val id = glGenTextures()
    val imageDetails = ImageDetails(data, id, width.get(), height.get(), components.get())

    generateTexture(imageDetails)

    if (data != null) {
        stbi_image_free(data)
    }

    return Image(id)
}

private fun defaultGenerateTexture(details: ImageDetails) {
    glBindTexture(GL_TEXTURE_2D, details.id)
    glGenerateMipmap(GL_TEXTURE_2D)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, details.width, details.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, details.data)
}

private data class ImageDetails(val data: ByteBuffer?, val id: Int, val width: Int, val height: Int, val components: Int)

fun loadTextureDirectory(directory: String) = File(directory).walk().filter { it.isFile }.map { loadTexture(it.absolutePath) }.toList()

fun loadAnimation(directory: String, frameRate: Double = 24.0) = FrameAnimation(loadTextureDirectory(directory), frameRate)

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

fun ioResourceToByteBuffer(resource: InputStream, bufferSize: Int): ByteBuffer {
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
    return FontType(loadTexture(Res.font["$name.png"].path), Res.font["$name.fnt"].path)
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


private fun createVAO(): Int {
    val vaoID = GL30.glGenVertexArrays()
    vaos.add(vaoID)
    GL30.glBindVertexArray(vaoID)
    return vaoID
}


private fun unbindVAO() {
    GL30.glBindVertexArray(0)
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