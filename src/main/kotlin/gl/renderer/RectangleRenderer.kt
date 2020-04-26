package gl.renderer

import drawer.shader.DrawerScript
import drawer.shader.DrawerShader
import gl.models.Model
import gl.script.ShaderScript
import gl.shader.Shader
import gl.utils.createUnitSquareVecArray
import gl.vbo.AttributeArray
import gl.vbo.pointer.VBOPointer
import org.joml.Matrix4f
import org.joml.Vector3f
import util.colors.Color
import util.colors.toColor
import util.extensions.toFloatArray

class RectangleRenderer {

    private val positions = AttributeArray(createUnitSquareVecArray().toFloatArray(3), VBOPointer.position)
    val model = Model(positions)
    val transformation: Matrix4f = Matrix4f().identity()

    val vertex = object : DrawerScript() {
        //language=GLSL
        override val main: String =
                """
                out vec2 pos;
                void main(void) {
                    pos = $position.xy;
                    gl_Position = matrices(vec4($position, 1.0));
                }
                """

    }

    private val defaultFragment = object : DrawerScript() {


        //language=GLSL
        override val main: String = """
                out vec4 fragColor;
                in vec2 pos;       
                void main(void) {
                    vec2 st = pos - vec2(0.5);
                    
                    float height = $size.y;
                    float width = $size.x;
                    
                    st = vec2(st.x*width, st.y*height);
                    
                    float smoothStroke = 0.06*pixelSize;
                    float radius = max($radius, smoothStroke);

                    vec2 relative = abs(st) - vec2(0.5*width - radius, 0.5*height - radius);
                    float distance = length(max(relative, 0.0));
                    
                    float smoothStart = radius - smoothStroke;
                    float smoothEnd = radius;
                    
                    float alpha = 1.0 - smoothstep(smoothStart, smoothEnd, distance);
                    
                    fragColor = vec4($color.rgb, alpha);
                }
            """

    }

    val fragment = defaultFragment

    val shader: Shader by lazy { DrawerShader(vertex, fragment) }

    var color: Color
        get() = defaultFragment.color.value.toColor()
        set(value) {
            defaultFragment.color.set(value)
        }
    var radius: Float
        get() = defaultFragment.radius.value
        set(value) {
            defaultFragment.radius.value = value
        }

    private val vec3 = Vector3f()
    fun render(model: Model = this.model, transformation: Matrix4f = this.transformation) {
        transformation.getScale(vec3)
        defaultFragment.size.set(vec3.x, vec3.y)
        shader.render(model, transformation)
        transformation.identity()
    }


}