package gl.renderer

import gl.models.Model
import gl.script.ShaderScript
import gl.shader.Shader
import gl.utils.loadImage
import gl.vbo.AttributeArray
import gl.vbo.pointer.VBOPointer
import graphics.Image
import org.intellij.lang.annotations.Language
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL20
import resources.Resource
import util.colors.Color
import util.colors.toColor
import util.extensions.fill
import util.units.Vector

class PathRenderer : Renderer() {

    override val vertex = object : ShaderScript() {

        //language=GLSL
        override val main: String =
                """
                out vec2 tc;
                layout (binding=0) uniform sampler2D circleAtlas;

                void main(void) {
                    gl_Position = vec4($position, 1.0);
                }
                """

    }

    private val lineGeometry = object : ShaderScript() {

        val width = uniform.float(1f)

        @Language("GLSL")
        override val main: String = """
            layout (lines) in;
            layout (triangle_strip, max_vertices = 4) out;
                        
            void main() {
                vec2 p1 = gl_in[0].gl_Position.xy;
                vec2 p2 = gl_in[1].gl_Position.xy;
                vec2 rel = p2 - p1;
                float r = length(rel);
                float ratio = 0.5*$width/r;
                
                float x = rel.y*ratio;
                float y = -rel.x*ratio;

                vec4 rightAdjustment = vec4(x, y, 0.0, 0.0);
                vec4 leftAdjustment = vec4(-x, -y, 0.0, 0.0);
                
                gl_Position = matrices(gl_in[0].gl_Position + rightAdjustment); 
                EmitVertex();

                gl_Position = matrices(gl_in[0].gl_Position + leftAdjustment);
                EmitVertex();

                gl_Position = matrices(gl_in[1].gl_Position + rightAdjustment);
                EmitVertex();

                gl_Position = matrices(gl_in[1].gl_Position + leftAdjustment);
                EmitVertex();
                                
                EndPrimitive();
            }
        """
    }

    private val circleGeometry = object : ShaderScript() {

        val width = uniform.float(1f)

        @Language("GLSL")
        override val main: String = """
            layout (points) in;
            layout (triangle_strip, max_vertices = 4) out;
                        
            out GS_OUT
            {
                vec2 v_TexCoord;
            } gs_out;
                        
            void main() {
                vec4 p = gl_in[0].gl_Position;
                float l = $width/2.0;
                                
                gl_Position = matrices(p + vec4(-l, -l, 0.0, 0.0));
                gs_out.v_TexCoord = vec2(-0.0, -0.0);
                EmitVertex();

                gl_Position = matrices(p + vec4(l, -l, 0.0, 0.0));
                gs_out.v_TexCoord = vec2(1.0, -0.0);
                EmitVertex();

                gl_Position = matrices(p + vec4(-l, l, 0.0, 0.0));
                gs_out.v_TexCoord = vec2(-0.0, 1.0);
                EmitVertex();

                gl_Position = matrices(p + vec4(l, l, 0.0, 0.0));
                gs_out.v_TexCoord = vec2(1.0, 1.0);
                EmitVertex();
                                
                EndPrimitive();
            }
        """
    }


    private val _fragment = object : ShaderScript() {

        val mode = uniform.float(0f)

        //language=GLSL
        override val main: String = """
                out vec4 fragColor;
                in GS_OUT
                {
                    vec2 v_TexCoord;
                } fs_in;
                
                layout (binding=0) uniform sampler2D circleAtlas;   
                
                const float border = 0.01;
                const float edge = 0.99;

                void main(void) {
                    if ($mode == 0.0) {
                        fragColor = $color;
                    } else {
                        vec2 st = (fs_in.v_TexCoord - vec2(0.5));
                        float distance = dot(st, st)*2.0;
                        
                        float alpha = 1.0 - step(0.5 , distance);
                        if (alpha < 0.9*$color.a) {
                            discard; 
                        }         
                        fragColor = vec4($color.rgb, alpha*$color.a);
                    }
                }
            """
    }
    override val fragment = _fragment


    private val lineShader = Shader(vertex, fragment, geometry = lineGeometry)
    private val circleShader = Shader(vertex, fragment, geometry = circleGeometry)

    override var color: Color
        get() = _fragment.color.value.toColor()
        set(value) {
            _fragment.color.set(value)
        }

    var stroke: Float
        get() = lineGeometry.width.value
        set(value) {
            lineGeometry.width.value = value
            circleGeometry.width.value = value
        }

    var path = ArrayList<Vector>()

    private var floats: FloatArray

    private val vbo: AttributeArray
    override val model: Model

    private var isCircle = false
        set(value) {
            if (value) {
                _fragment.mode.value = 1f
            } else {
                _fragment.mode.value = 0f
            }
            field = value
        }

    init {
        val initialVertices = 300
        floats = FloatArray(initialVertices*3)
        vbo = AttributeArray(initialVertices, VBOPointer.position)
        model = Model(vbo) {
            GL20.glStencilOp(GL20.GL_KEEP, GL20.GL_KEEP, GL20.GL_REPLACE)
            GL20.glStencilFunc(GL20.GL_NOTEQUAL, 1, 0xFF)
            GL20.glStencilMask(0xFF)

            if (!isCircle) {
                glDrawArrays(GL_LINE_STRIP, 0, it.vertexCount)
            } else {
                glBindTexture(GL_TEXTURE_2D, it.image.id)
                glDrawArrays(GL_POINTS, 0, it.vertexCount)
            }

            GL20.glStencilFunc(GL20.GL_ALWAYS, 0, 0xFF)
        }
    }

    override fun render(model: Model, transformation: Matrix4f) {
        fillFloats(path)
        this.model.vertexCount = path.size

        GL20.glClear(GL20.GL_STENCIL_BUFFER_BIT)
        isCircle = false
        lineShader.render(this.model, transformation)
        isCircle = true
        circleShader.render(this.model, transformation)
    }

    private fun fillFloats(path: ArrayList<Vector>) {
        if (path.size*3 >= floats.size) {
            floats = FloatArray(floats.size*2)
        }
        floats.fill(path)
        vbo.update(floats)
    }


}