package gl.utils

import gl.vbo.AttributePointer
import gl.script.Declarations
import gl.vbo.AttributeBuffer
import graphics.Image
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.*
import org.lwjgl.opengl.GL11.GL_RGBA
import org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT
import org.lwjgl.stb.STBImage
import resources.Resource
import util.extensions.vec
import util.units.Vector
import java.nio.ByteBuffer


val positionAttribute = AttributePointer.create(0, 3)
val texCoordsAttribute = AttributePointer.create(1, 2)

internal fun startGame() {
    val vao = GL30.glGenVertexArrays()
    GL30.glBindVertexArray(vao)

    //language=GLSL
    Declarations.function("""
        vec4 matrices(vec4 position) {
            return projection*view*transformation*position;
        }
    """)
}

internal fun startFrame() {
    GL20.glClear(GL20.GL_COLOR_BUFFER_BIT or GL_STENCIL_BUFFER_BIT)
    GL11.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)

    GL11.glEnable(GL11.GL_STENCIL_TEST)
    enableAlphaBlending()
}

fun createUnitSquareArray() = createQuadArray(0f, 1f, 1f, 0f)

fun loadTextureUnitSquare() = loadTextureQuad(0f, 1f, 1f, 0f)

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

private fun enableAlphaBlending() {
    GL11.glEnable(GL11.GL_BLEND)
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
}


private fun createQuadArray(left: Float, top: Float, right: Float, bottom: Float): Array<Vector> {
    return arrayOf(
            vec(left, top),
            vec(left, bottom),
            vec(right, bottom),
            vec(left, top),
            vec(right, bottom),
            vec(right, top))

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

private fun loadTextureQuad(left: Float, top: Float, right: Float, bottom: Float): Array<Vector> {
    return arrayOf(
            vec(left, bottom),
            vec(left, top),
            vec(right, top),
            vec(left, bottom),
            vec(right, top),
            vec(right, bottom))

}

fun loadImage(resource: Resource): Image {
    val widthBuffer = BufferUtils.createIntBuffer(1)
    val heightBuffer = BufferUtils.createIntBuffer(1)
    val componentsBuffer = BufferUtils.createIntBuffer(1)

    val data = STBImage.stbi_load_from_memory(resource.buffer, widthBuffer, heightBuffer, componentsBuffer, 4)
    val width = widthBuffer.get()
    val height = heightBuffer.get()

    var image = Image(0)
    if (data != null) {
        image = loadImage(data, width, height)
        STBImage.stbi_image_free(data)
    }

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
