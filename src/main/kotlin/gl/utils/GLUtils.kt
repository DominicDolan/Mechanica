package gl.utils

import gl.vbo.AttributePointer
import gl.script.Declarations
import gl.vbo.AttributeBuffer
import graphics.Image
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.*
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

val defaultDrawProcedure: (AttributeBuffer) -> Unit = {
    GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, it.vertexCount)

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

    val image = GL11.glGenTextures()
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, image)

    val data = STBImage.stbi_load_from_memory(resource.buffer, widthBuffer, heightBuffer, componentsBuffer, 4)
    val width = widthBuffer.get()
    val height = heightBuffer.get()

    if (data != null) {
        setMipmapping(data, width, height, 4)

        STBImage.stbi_image_free(data)
    }

    return Image(image)
}

private fun setMipmapping(data: ByteBuffer, width: Int, height: Int, levels: Int) {
    for (i in 0..levels) {
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, i, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data)
    }
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR)
    GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D)
    GL11.glTexParameteri (GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, levels)

}
