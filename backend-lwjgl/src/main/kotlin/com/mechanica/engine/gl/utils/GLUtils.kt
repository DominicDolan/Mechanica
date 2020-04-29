package com.mechanica.engine.gl.utils

import com.mechanica.engine.resources.Resource
import com.mechanica.engine.unit.vector.Vector
import com.mechanica.engine.unit.vector.vec
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.GL_RGBA
import org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT
import org.lwjgl.opengl.GL12
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import org.lwjgl.stb.STBImage
import java.nio.ByteBuffer


internal fun startFrame() {
    GL20.glClear(GL20.GL_COLOR_BUFFER_BIT or GL_STENCIL_BUFFER_BIT)
//
//    val pvMatrix = Game.viewMatrix.getWithProjection(Game.projectionMatrix)
//    val pvUiMatrix = Game.uiViewMatrix.getWithProjection(Game.projectionMatrix)
//    Game.pvMatrix.set(pvMatrix)
//    Game.pvUiMatrix.set(pvUiMatrix)
//
}

fun createUnitSquareVecArray() = createQuadVecArray(0f, 1f, 1f, 0f)
fun createUnitSquareFloatArray() = createQuadFloatArray(0f, 1f, 1f, 0f)

fun createTextureUnitSquareVecArray() = createTextureQuadVecArray(0f, 1f, 1f, 0f)
fun createTextureUnitSquareFloatArray() = createTextureQuadFloatArray(0f, 1f, 1f, 0f)

/**
 *  Creates an array of shorts of size numberOfQuads*6 to be used with a series of quadrilaterals
 *
 *  For the first quad, the array will have indices in the form of:
 *
 *      0, 1, 2,
 *      2, 1, 3
 *  For the second set it will be:
 *
 *      4, 5, 6,
 *      6, 5, 7
 *  i.e. the first set + 4
 *  and so on
 *
 *  @param numberOfQuads the number of quadrilaterals to be represented by the index array
 *  @return The short array filled with the indices data for the required number of quads
 *
 *
 */
fun createIndicesArrayForQuads(numberOfQuads: Int): ShortArray {
    val array = ShortArray(numberOfQuads*6)
    val first = shortArrayOf(
            0, 1, 2, //First triangle
            2, 1, 3  //Second Triangle
    )
    for (i in 0 until numberOfQuads) {
        for (j in first.indices) {
            array[i*6 + j] = (first[j] + i*4).toShort()
        }
    }
    return array
}

fun enableAlphaBlending() {
    GL11.glEnable(GL11.GL_BLEND)
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
}


private fun createQuadVecArray(left: Float, top: Float, right: Float, bottom: Float): Array<Vector> {
    return arrayOf(
            vec(left, top),
            vec(left, bottom),
            vec(right, bottom),
            vec(left, top),
            vec(right, bottom),
            vec(right, top))

}

fun createQuadFloatArray(left: Float, top: Float, right: Float, bottom: Float): FloatArray {
    return floatArrayOf(
            left, top, 0f,
            left, bottom, 0f,
            right, bottom, 0f,
            left, top, 0f,
            right, bottom, 0f,
            right, top, 0f)

}

/*

val indices = shortArrayOf(0, 1, 2, 0, 2, 3) // The order of vertexrendering.

vertices = floatArrayOf(
    0f, 1f, //V0 top left
    0f, 0f, //V1 bottom left
    1f, 0f, //V2 bottom right
    1f, 1f  //V3 top right
)

textureCoords = floatArrayOf(
        0f, 0f, //V0
        0f, 1f, //V1
        1f, 1f, //V2
        1f, 0f  //V3
)

*/

private fun createTextureQuadVecArray(left: Float, top: Float, right: Float, bottom: Float): Array<Vector> {
    return arrayOf(
            vec(left, bottom),
            vec(left, top),
            vec(right, top),
            vec(left, bottom),
            vec(right, top),
            vec(right, bottom))

}

private fun createTextureQuadFloatArray(left: Float, top: Float, right: Float, bottom: Float): FloatArray {
    return floatArrayOf(
            left, bottom, 0f,
            left, top, 0f,
            right, top, 0f,
            left, bottom, 0f,
            right, top, 0f,
            right, bottom, 0f)

}

fun loadImage(resource: Resource): Image {
    val data = ImageData(resource)

    var image = Image(0)
    if (data.data != null) {
        image = loadImage(data.data, data.width, data.height)
    }
    data.free()

    return image
}


fun loadImage(buffer: ByteBuffer, width: Int, height: Int, levels: Int = 4, format: Int = GL_RGBA): Image {

    val image = GL11.glGenTextures()
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, image)

    setMipmapping(buffer, width, height, levels, format)

    return Image(image)
}

private fun setMipmapping(data: ByteBuffer, width: Int, height: Int, levels: Int, format: Int) {
    for (i in 0..levels) {
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, i, format, width, height, 0, format, GL11.GL_UNSIGNED_BYTE, data)
    }
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR)
    GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D)
    GL11.glTexParameteri (GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, levels)

}

class ImageData(resource: Resource) {
    val data: ByteBuffer?
    val width: Int
    val height: Int

    init {
        val widthBuffer = BufferUtils.createIntBuffer(1)
        val heightBuffer = BufferUtils.createIntBuffer(1)
        val componentsBuffer = BufferUtils.createIntBuffer(1)

        data = STBImage.stbi_load_from_memory(resource.buffer, widthBuffer, heightBuffer, componentsBuffer, 4)
        width = widthBuffer.get()
        height = heightBuffer.get()
    }

    fun free() {
        if (data != null) {
            STBImage.stbi_image_free(data)
        }
    }
}
