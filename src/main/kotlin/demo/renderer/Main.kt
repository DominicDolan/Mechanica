package demo.renderer

import display.Game
import display.GameOptions
import graphics.Image
import graphics.drawer.Drawer
import input.Cursor
import loader.loadImageFromResource
import loader.loadTexture
import loader.loadTexturedQuad
import org.lwjgl.opengl.GL11
import resources.Res
import shader.Shader
import shader.script.Declarations
import shader.script.ShaderScript
import state.State
import util.colors.hex
import util.colors.hsl
import util.colors.rgba

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
    private val quad = loadTexturedQuad(loadImageFromResource(Res.image["colors"]), 0f, 0.5f, 0.5f, 0f)
    private val red = rgba(1.0, 0.0, 0.0, 1.0)

    private val vertex = object : ShaderScript() {
        //language=GLSL
        override val main: String =
                """
                out vec2 pass_textureCoords;
                void main(void) {
                    gl_Position = vec4(position.xyz, 1.0);
	                pass_textureCoords = textureCoords;
                }
                """

    }

    private val fragment = object : ShaderScript() {

        val color = uniform.vec4(hex(0xFF00FFFF))
        //language=GLSL
        override val main: String = """
            
                in vec2 pass_textureCoords;
                out vec4 out_Color;
                
                uniform sampler2D textureSampler;
                
                void main(void) {
                    out_Color = texture(textureSampler, pass_textureCoords);
                }
            """

    }

    private val shader: Shader

    init {
        shader = Shader(vertex, fragment)
        shader.drawProcedure = {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, it.texture.id)
            GL11.glDrawElements(GL11.GL_TRIANGLES, it.vertexCount,
                    GL11.GL_UNSIGNED_SHORT, 0)
        }
        Declarations.addGlobal {
            val newVar = uniform.mat4()
        }
        println(vertex.script)
    }

    override fun update(delta: Double) {

    }

    override fun render(draw: Drawer) {
        val blend = (Cursor.viewX/Game.viewWidth) + 0.5
        val adjusted = hsl(red.hue, blend, red.lightness)
        fragment.color.set(adjusted)
        shader.render(quad)
    }
}