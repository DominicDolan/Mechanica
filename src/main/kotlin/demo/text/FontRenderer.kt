package demo.text

import gl.renderer.Renderer
import gl.script.ShaderScript
import gl.shader.Shader
import gl.utils.createIndicesArrayForQuads
import gl.utils.loadImage
import gl.utils.positionAttribute
import gl.utils.texCoordsAttribute
import gl.vbo.MutableVBO
import gl.vbo.VBO
import graphics.Image
import loader.toBuffer
import models.Model
import org.joml.Matrix4f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.GL_UNSIGNED_SHORT
import org.lwjgl.stb.*
import org.lwjgl.system.MemoryStack
import resources.Res
import resources.Res.font
import util.colors.Color
import util.colors.hex
import util.colors.toColor
import java.nio.ByteBuffer


class FontRenderer: Renderer {

    private val vertex = object : ShaderScript() {
        //language=GLSL
        override val main: String =
                """
                out vec2 tc;
                layout (binding=0) uniform sampler2D samp;
                
                void main(void) {
                    gl_Position = matrices(vec4($position, 1.0));
                    tc = $textureCoords;
                }
                """

    }

    private val fragment = object : ShaderScript() {

        val color = uniform.vec4(hex(0x504050FF))

        //language=GLSL
        override val main: String = """
                in vec2 tc;
                layout (binding=0) uniform sampler2D samp;
                out vec4 out_Color;
                                
                void main(void) {
                    vec4 texColor = texture(samp, tc);
                    out_Color = vec4($color.rgb, texColor.a*$color.a);
//                    out_Color = vec4(1.0, 0.0, 0.0, 1.0);
                }
            """

    }

    private val shader = Shader(vertex, fragment)
    private val fontMap = HashMap<Font, TextModel>()

    var color: Color
        get() = fragment.color.value.toColor()
        set(value) {
            fragment.color.set(value)
        }

    var text: String
        get() = model.text
        set(value) { model.text = value }

    var font: Font = Font(Res.font["RobotoMono-Regular.ttf"])
        set(value) {
            val newModel = TextModel(font)
            newModel.text = text
            fontMap.putIfAbsent(value, newModel)
            field = value
        }

    private val model: TextModel
        get() = fontMap[font] ?: TextModel(font).also { fontMap[font] = it }

    override fun render(model: Model, transformation: Matrix4f) {
        shader.render(this.model, transformation)
    }

}