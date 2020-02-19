package gl.renderer

import gl.Drawable
import gl.utils.createTexture
import gl.script.ShaderScript
import gl.shader.Shader
import graphics.Image
import org.joml.Matrix4f
import resources.InternalResource
import util.colors.Color
import util.colors.hex
import util.colors.toColor

class CircleRenderer : Renderer {
    private val vertex = object : ShaderScript() {
        //language=GLSL
        override val main: String =
                """
                out vec2 tc;
                layout (binding=0) uniform sampler2D circleAtlas;

                void main(void) {
                    gl_Position = matrices(vec4(position, 1.0));
                    tc = textureCoords;
                }
                """

    }

    private val fragment = object : ShaderScript() {

        val color = uniform.vec4(hex(0xFF00FFFF))
        val strokeWidth = uniform.float(1.0f)

        //language=GLSL
        override val main: String = """
                out vec4 color;
                in vec2 tc;
                layout (binding=0) uniform sampler2D circleAtlas;   
                
                const float border = 0.03;
                const float edge = 0.97;

                void main(void) {
                    float innerEdge = edge - $strokeWidth;
                    float distance = 1.0 - texture(circleAtlas, tc).a;
                    float alpha1 = (smoothstep(innerEdge, innerEdge + border, distance))*$color.a;
                    float alpha2 = (1.0 - smoothstep(edge, edge + border, distance))*$color.a;
                    float alpha = 0;
                    if ($strokeWidth >= 1.0){
                        alpha = alpha2;
                    } else {
                        alpha = alpha1*alpha2;
                    }
                
                    color = vec4($color.rgb, alpha);
                }
            """

    }

    private val shader: Shader = Shader(vertex, fragment)

    private val oval: Image = createTexture(InternalResource("res/images/oval.png"))

    var color: Color
        get() = fragment.color.value.toColor()
        set(value) {
            fragment.color.set(value)
        }

    override fun render(drawable: Drawable, transformation: Matrix4f) {
        drawable.image = oval
        shader.render(drawable, transformation)
    }
}