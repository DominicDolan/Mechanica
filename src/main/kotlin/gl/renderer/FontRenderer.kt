package gl.renderer

import font.FontType
import font.GUIText
import gl.Drawable
import gl.createTexture
import gl.script.ShaderScript
import gl.shader.Shader
import graphics.Image
import loader.loadFont
import org.joml.Matrix4f
import resources.InternalResource
import util.colors.Color
import util.colors.hex
import util.colors.toColor

class FontRenderer : Renderer {
    private val vertex = object : ShaderScript() {
        val translation = uniform.vec2(0.0, 0.0)

        //language=GLSL
        override val main: String =
                """
                out vec2 tc;
                layout (binding=0) uniform sampler2D fontAtlas;

                void main(void) {
                    gl_Position = matrices(vec4(position.xy + $translation, 0.0, 1.0));
                    tc = textureCoords;
                }
                """

    }

    private val fragment = object : ShaderScript() {

        val color = uniform.vec4(hex(0xFF00FFFF))

        //language=GLSL
        override val main: String = """
                out vec4 color;
                in vec2 tc;
                layout (binding=0) uniform sampler2D fontAtlas;   
                                
                const float edge = 0.5;
                const float border_fraction = 0.2;

                void main(void) {
                    float distance = 1.0 - texture(fontAtlas, tc).a;
                    float alpha = (1.0 - smoothstep(edge, edge + border_fraction*edge, distance))*$color.a;
                    color = vec4($color.rgb, alpha);
                }
            """

    }

    private val shader: Shader = Shader(vertex, fragment)


    val font: FontType = loadFont("arial")
    val guiText = GUIText("", 10f, font, 0f, 0f, 5f)


    var color: Color
        get() = fragment.color.value.toColor()
        set(value) {
            fragment.color.set(value)
        }

    override fun render(drawable: Drawable, transformation: Matrix4f) {
        vertex.translation.set(guiText.positionX, guiText.positionY)
        drawable.image = Image(guiText.model.texture.id)
        shader.render(drawable, transformation)
    }
}