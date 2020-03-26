package gl.renderer

import display.Game
import gl.models.Model
import gl.script.ShaderDeclarations
import gl.script.ShaderScript
import gl.utils.createUnitSquareArray
import gl.utils.loadTextureUnitSquare
import gl.vbo.AttributeArray
import gl.vbo.pointer.VBOPointer
import org.joml.Matrix4f
import org.joml.Vector4f
import org.lwjgl.opengl.GL11
import util.colors.Color
import util.colors.hex
import util.colors.toColor
import util.extensions.toFloatArray
import kotlin.math.sqrt

class CircleRenderer : Renderer() {

    override val vertex = object : ShaderScript() {
        val resolution = uniform.vec2(Game.width.toDouble(), Game.height.toDouble())
        //language=GLSL
        override val main: String =
                """
                out vec2 tc;
                out vec2 pc;
                layout (binding=0) uniform sampler2D circleAtlas;

                void main(void) {
                    gl_Position = pvMatrix*transformation*vec4($position, 1.0);
                    tc = $textureCoords;
                }
                """

    }

    private val _fragment = object : ShaderScript() {

        val scale = uniform.float()
        val strokeWidth = uniform.float(1.0f)

        //language=GLSL
        override val main: String = """
                out vec4 fragColor;
                in vec2 tc;
                
                const float border = 0.03;
                const float edge = 0.97;

                void main(void) {
                    float innerEdge = edge - $strokeWidth;
                    vec2 st = (tc - vec2(0.5));
                    float distance = length(st);
                    
                    float alpha = 1.0 - smoothstep(0.5 - 0.2*pixelScale, 0.5 , distance);
                                        
                    fragColor = vec4($color.rgb, alpha);
                }
            """

    }

    override val fragment: ShaderScript = _fragment

    private val positionBuffer = AttributeArray(createUnitSquareArray().toFloatArray(3), VBOPointer.position)
    private val textureBuffer = AttributeArray(loadTextureUnitSquare().toFloatArray(2), VBOPointer.texCoords)

    var vec4 = Vector4f()
    override val model: Model = Model(positionBuffer, textureBuffer) {
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, it.vertexCount)
    }

    override var color: Color
        get() = _fragment.color.value.toColor()
        set(value) {
            _fragment.color.set(value)
        }
    var strokeWidth: Float
        get() = _fragment.strokeWidth.value
        set(value) {
            _fragment.strokeWidth.value = value
        }

    override fun render(model: Model, transformation: Matrix4f) {
        shader.render(this.model, transformation, projection, view)
    }
}