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
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL30.glBindVertexArray
import org.lwjgl.opengl.GL30.glGenVertexArrays
import gl.shader.Shader
import gl.script.ShaderScript
import gl.startFrame
import graphics.drawer.DrawerImpl
import state.State
import util.colors.hex
import util.colors.hsl
import util.colors.rgba
import util.extensions.degrees
import util.extensions.vec
import util.units.Vector


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
                    
                void main(void) {
                    gl_Position = model*vec4(position, 1.0);
                }
                """

    }

    private val fragment = object : ShaderScript() {

        val color = uniform.vec4(hex(0xFF00FFFF))
        //language=GLSL
        override val main: String = """
            
                in vec2 pass_textureCoords;
                out vec4 out_Color;
                                
                void main(void) {
                    out_Color = $color;
                }
            """

    }

    private val renderer: Renderer
    private val vbo: VBO
    private val transformationMatrix = TransformationMatrix()
    val draw = DrawerImpl()
    init {
        startGame()
        transformationMatrix.setScale(1.0, 1.0,1.0)
        val vertices = loadQuad(0.5f, 1f, 1f, 0.5f)

        vbo = VBO.create(vertices, positionAttribute)

        renderer = object : Renderer() {
            override val shader: Shader = Shader(vertex, fragment)

            override fun draw(vbo: VBO) {
                GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vbo.vertexCount)
            }
        }

        println(vertex.script)
    }

    override fun update(delta: Double) {

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
//        transformationMatrix.setScale(scaleX, scaleY, 1.0)
//
//        renderer.render(vbo, transformationMatrix.create())
        this.draw.rotated(30.degrees).centered.red.rectangle(0, 0, 1.0, 1.0)

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
}