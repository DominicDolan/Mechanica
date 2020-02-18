package demo.renderer

import display.Game
import display.GameOptions
import gl.*
import graphics.Image
import graphics.drawer.Drawer
import input.Cursor
import matrices.TransformationMatrix
import models.Model
import org.lwjgl.opengl.GL11
import gl.script.ShaderScript
import gl.shader.ShaderImpl
import gl.startFrame
import gl.vbo.AttributeBuffer
import gl.vbo.VBO
import graphics.drawer.DrawerImpl
import input.Keyboard
import org.lwjgl.BufferUtils
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
            .setResolution(1280, 720)
//            .setFullscreen(true, true)
            .setDebugMode(true)
            .setViewPort(height = 10.0)
            .setStartingState { StartMain() }

    Game.start(options)
    Game.update()
    Game.destroy()
}

private class StartMain : State() {
    private val quad: Model = Model(-1, 0, Image(-1))
    //= loadTexturedQuad(loadImageFromResource(Res.image["colors"]), 0f, 0.5f, 0.5f, 0f)
    private val red = rgba(1.0, 0.0, 0.0, 1.0)

    private val vertex = object : ShaderScript() {
        //language=GLSL
        override val main: String =
                """
                out vec2 tc;
                layout (binding=0) uniform sampler2D samp;
                void main(void) {
                    gl_Position = matrices(vec4(position, 1.0));
                    tc = textureCoords;
                }
                """

    }

    private val fragment = object : ShaderScript() {

        val color = uniform.vec4(hex(0xFF00FFFF))
        //language=GLSL
        override val main: String = """
            
                in vec2 tc;
                out vec4 out_Color;
                layout (binding=0) uniform sampler2D samp;
                                
                void main(void) {
                    out_Color = texture(samp, tc);
                }
            """

    }

//    private val renderer: Renderer
    private val vbo: VBO
    private val texVbo: VBO
    val shader: ShaderImpl
    private val transformation = TransformationMatrix()
    val draw = DrawerImpl()
    val drawable: Drawable
    val image: Image
    var timer = 0.0
    var score = 0
    init {
        startGame()
        shader = ShaderImpl(vertex, fragment)
        transformation.setScale(1.0, 1.0,1.0)

        val vertices = loadQuad(0f, 1f, 1f, 0f)
        val texVerts = loadQuad(0f, 1.0f, 1.0f, 0f)

        vbo = VBO.create(vertices, positionAttribute)
        texVbo = VBO.create(texVerts, texCoordsAttribute)
        image = createTexture(Res.image["colors"])

        drawable = Drawable(vbo, texVbo)
        drawable.image = image


//
//        renderer = object : Renderer() {
//            override val shader: Shader = Shader(vertex, fragment)
//
//            override fun draw(drawable: Drawable) {
//                texVbo.bind()
//                GL13.glActiveTexture(GL_TEXTURE0)
//                GL11.glBindTexture(GL11.GL_TEXTURE_2D, drawable.image.id)
//                GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, drawable.vertexCount)
//            }
//        }

        println(vertex.script)
    }

    override fun update(delta: Double) {
        timer += delta
        if (Keyboard.MB1.hasBeenPressed) {
            score++
        }
        if (Keyboard.MB2.hasBeenPressed) {
            score--
        }
    }

    override fun render(draw: Drawer) {

        startFrame()

        val blend = (((Cursor.viewX / Game.viewWidth) + 0.5)*360).degrees
//
//        val adjusted = hsl(blend, red.saturation, red.lightness)
//        fragment.color.set(adjusted)
//
//        val scaleX = ((Cursor.viewX*2.0 / Game.viewWidth))
//        val scaleY = ((Cursor.viewY*2.0 / Game.viewHeight))
        transformation.setScale(4.0, 4.0, 1.0)
        transformation.setTranslate(0.0, -4.0, 0.0)

//        shader.render(drawable, transformation.create())

        this.draw.red.text("Score: $score", 1f + (score.toFloat()/10f), 0, 0)
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