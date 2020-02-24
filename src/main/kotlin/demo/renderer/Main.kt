package demo.renderer

import display.Game
import display.GameOptions
import drawer.Drawer
import models.Model
import gl.script.ShaderScript
import gl.utils.IndexedVertices
import gl.utils.loadImage
import gl.utils.positionAttribute
import gl.utils.startFrame
import gl.vbo.VBO
import graphics.Image
import graphics.Polygon
import input.Cursor
import input.Keyboard
import loader.toBuffer
import matrices.TransformationMatrix
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30
import org.lwjgl.stb.STBImage
import resources.Res
import state.State
import util.colors.hex
import util.colors.rgba
import util.extensions.degrees
import util.extensions.vec
import util.units.Vector
import java.nio.ByteBuffer


fun main() {
    val options = GameOptions()
            .setResolution(1920, 1080)
//            .setFullscreen(true, true)
            .setDebugMode(true)
            .setViewPort(height = 10.0)
            .setStartingState { StartMain() }

    Game.start(options)
    Game.update()
    Game.destroy()
}

private class StartMain : State() {
    //= loadTexturedQuad(loadImageFromResource(Res.image["colors"]), 0f, 0.5f, 0.5f, 0f)
    private val red = rgba(1.0, 0.0, 0.0, 1.0)

    private val vertex = object : ShaderScript() {
        //language=GLSL
        override val main: String =
                """
                void main(void) {
                    gl_Position = matrices(vec4(position, 1.0));
                }
                """

    }

    private val fragment = object : ShaderScript() {

        val color = uniform.vec4(hex(0xFF00FFFF))
        //language=GLSL
        override val main: String = """
            
                out vec4 out_Color;
                                
                void main(void) {
                    out_Color = $color;
                }
            """

    }

//    private val renderer: Renderer
//    private val vbo: VBO
//    private val texVbo: VBO
//    val shader: ShaderImpl
    private val transformation = TransformationMatrix()
//    val drawable: Drawable
    val image: Image
    var timer = 0.0
    var score = 0
    val polygon: Polygon

    init {
//        shader = ShaderImpl(vertex, fragment)
        transformation.setScale(1.0, 1.0,1.0)

//        val vertices = loadQuad(0f, 1f, 1f, 0f)
//        val texVerts = loadQuad(0f, 1.0f, 1.0f, 0f)

//        vbo = VBO.create(vertices, positionAttribute)
//        texVbo = VBO.create(texVerts, texCoordsAttribute)
        image = loadImage(Res.image["colors"])
//
//        drawable = createIndexedDrawable(0f, 1f, 1f, 0f)


        val square = listOf(
                vec(0, 0),
                vec(0, 1),
                vec(1, 1),
                vec(1, 0)
        )

        val random = listOf(
                vec(0, 0),
                vec(0, 0.4),
                vec(1, 0.5),
                vec(4, 2),
                vec(3.5, -1),
                vec(3, -1.5),
                vec(1, -1)
        )

        polygon = Polygon.create(random)

    }

    override fun update(delta: Double) {
        timer += delta
        if (Keyboard.MB1.hasBeenPressed) {
            score++
        }
        if (Keyboard.MB2.hasBeenPressed) {
            score--
        }
        if (Keyboard.A.isDown) {
            Game.viewX -= 3.0 * delta
        }
        if (Keyboard.D.isDown) {
            Game.viewX += 3.0 * delta
        }
        if (Keyboard.W.isDown) {
            Game.viewY += 3.0 * delta
        }
        if (Keyboard.S.isDown) {
            Game.viewY -= 3.0 * delta
        }
    }

    override fun render(draw: Drawer) {

        val cursorFraction = ((Cursor.viewX / Game.viewWidth) + 0.5)
        val blend = (cursorFraction*360).degrees

        draw.stroke(0.1).blue.polygon(polygon, scaleWidth = cursorFraction*3.0)
        draw.centered.rotated(blend).about(0, 1).image(image, 0, 0, 1, 1)
        draw.normal.image(image, -0.5, -0.5, 1, 1)
        draw.stroke(0.1).red.circle(0, 3, 1.0)
        draw.stroke(0.1).green.line(vec(4, 3), vec(Cursor.worldX, Cursor.worldY))
        draw.red.rectangle(0, -1, 4, 1)
        draw.blue.text("HELLO, WORLD", 1.0, 0, 0)
    }

    private fun loadQuad(left: Float, top: Float, right: Float, bottom: Float): Array<Vector> {
        return arrayOf(
                vec(left, top),
                vec(left, bottom),
                vec(right, bottom),
                vec(left, top),
                vec(right, top),
                vec(right, bottom))

    }

    fun createIndexedDrawable(left: Float, top: Float, right: Float, bottom: Float): Model {
        val vertices = createIndexedQuad(left, top, right, bottom)

        val vertexVBO = VBO.create(vertices.vertices.toBuffer(), positionAttribute)
        val indices = VBO.createIndicesBuffer(vertices.indices.toBuffer())

        return Model(vertexVBO, indices) {
            GL11.glDrawElements(GL11.GL_TRIANGLES, it.vertexCount, GL11.GL_UNSIGNED_SHORT, 0)
        }
    }

    fun createIndexedQuad(left: Float, top: Float, right: Float, bottom: Float): IndexedVertices {
        val vertices = floatArrayOf(left, top, 0.0f, left, bottom, 0.0f, right, bottom, 0.0f, right, top, 0.0f)

        val indices = shortArrayOf(0, 1, 2, 0, 2, 3)

        return IndexedVertices(vertices, indices)
    }

    fun loadImageFromMemory(buffer: ByteBuffer): Image {
        val width = BufferUtils.createIntBuffer(1)
        val height = BufferUtils.createIntBuffer(1)
        val components = BufferUtils.createIntBuffer(1)

        val data = STBImage.stbi_load_from_memory(buffer, width, height, components, 4)
        val id = GL11.glGenTextures()
        val imageDetails = ImageDetails(data, id, width.get(), height.get(), components.get())

        loadersVersion(imageDetails)

        if (data != null) {
            STBImage.stbi_image_free(data)
        }

        return Image(id)
    }

    fun loadersVersion(details: ImageDetails) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, details.id)
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, details.width, details.height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, details.data)
    }

    private data class ImageDetails(val data: ByteBuffer?, val id: Int, val width: Int, val height: Int, val components: Int)

}