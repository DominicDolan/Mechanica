package demo.polygon

import geometry.triangulation.Triangulator
import gl.models.Model
import gl.script.ShaderScript
import gl.shader.Shader
import gl.vbo.AttributeArray
import gl.vbo.ElementIndexArray
import gl.vbo.pointer.AttributePointer
import gl.vbo.pointer.VBOPointer
import input.Cursor
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL40.*
import util.colors.Color
import util.colors.hex
import util.colors.toColor
import util.extensions.fill
import util.extensions.toFloatArray
import util.extensions.vec
import util.units.LightweightVector
import util.units.Vector

class PolygonRenderer2 {

    val typesAttribute = AttributePointer.create(2, 1)
    private val vertex = object : ShaderScript() {
        val types = attribute(typesAttribute).float()
        //language=GLSL
        override val main: String =
                """
                out float pos;
                out vec2 index;
                void main(void) {
                    pos = $types;
                    gl_Position = matrices(vec4($position, 1.0));
                }
                """

    }

    private val fragment = object : ShaderScript() {

        val mouse = uniform.vec2(0.0, 0.0)

        //language=GLSL
        override val main: String = """
                out vec4 fragColor;
                in float pos;
                in vec2 index;
                                
                void main(void) {
                    float st = pos - 0.5;
                    
                    float smoothStroke = 0.1*pixelScale;
                    float radius = max(0.01, smoothStroke);

                    float relative = abs(st) - (0.5 - radius);
                    float distance = max(relative, 0.0);
                    
                    float smoothStart = radius - smoothStroke;
                    float smoothEnd = radius;
                    
                    float alpha = 1.0 - smoothstep(smoothStart, smoothEnd, distance);
                    
                    fragColor = vec4($color.rgb, alpha);
                }
            """

    }

    private val shader = Shader(vertex, fragment)
    private val startTime = System.currentTimeMillis()
    var color: Color
        get() = fragment.color.value.toColor()
        set(value) {
            fragment.color.set(value)
        }

    private val transformation = Matrix4f()

    private var floats: FloatArray
    var shorts: ShortArray

    private val vbo: AttributeArray
    private val types: AttributeArray
//    private val indices: ElementIndexArray

    private val model: Model

    init {
        val initialVertices = 300
        floats = FloatArray(initialVertices*3)
        shorts = ShortArray(initialVertices*3)

        vbo = AttributeArray(initialVertices, VBOPointer.position)
        types = AttributeArray(initialVertices, typesAttribute)
//        indices = ElementIndexArray(initialVertices)

        model = Model(vbo, types)

        color = hex(0x00FF0080)
    }

    fun render(path: List<LightweightVector>, model: Model = this.model) {
        fillFloats(path)
        fragment.mouse.set(Cursor.viewX, Cursor.viewY)
        shader.render(model, transformation)
    }

    fun render(triangulator: Triangulator, model: Model = this.model) {
        vbo.update(triangulator.floats)
        types.update(triangulator.types)
        fragment.mouse.set(Cursor.viewX, Cursor.viewY)
        shader.render(model, transformation)
    }

    private fun fillFloats(path: List<LightweightVector>) {
        if (path.size*3 >= floats.size) {
            floats = FloatArray(floats.size*2)
        }

        floats.fill(path)
        vbo.update(floats)
    }

    private fun fillFloats(floats: FloatArray) {
        vbo.update(floats)
    }

//    fun fillShorts(triangulator: Triangulator) {
//        shorts = triangulator.indices
//        model.vertexCount = triangulator.indexCount
//        indices.update(shorts)
//    }
}