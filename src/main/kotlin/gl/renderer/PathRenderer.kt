package gl.renderer

import gl.script.ShaderScript
import gl.shader.Shader
import gl.utils.loadImage
import gl.utils.positionAttribute
import gl.vbo.MutableVBO
import gl.vbo.VBO
import util.extensions.fill
import graphics.Image
import matrices.TransformationMatrix
import models.Model
import org.intellij.lang.annotations.Language
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL20
import resources.Resource
import util.colors.Color
import util.colors.hex
import util.colors.toColor
import util.units.Vector

class PathRenderer : Renderer {

    private val vertex = object : ShaderScript() {

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


    private val fragment = object : ShaderScript() {

        val color = uniform.vec4(hex(0xFF00FF88))
        val mode = uniform.float(0f)

        //language=GLSL
        override val main: String = """
                out vec4 color;
                in GS_OUT
                {
                    vec2 v_TexCoord;
                } fs_in;
                
                layout (binding=0) uniform sampler2D circleAtlas;   
                
                const float border = 0.01;
                const float edge = 0.99;

                void main(void) {
                    if ($mode == 0.0) {
                        color = $color;
                    } else {
                        float distance = 1.0 - texture(circleAtlas, fs_in.v_TexCoord).a;
                        float alpha = (1.0 - smoothstep(edge, edge + border, distance))*$color.a;
                        if (alpha < 0.9*$color.a) {
                            discard; 
                        }
                        color = vec4($color.rgb, alpha);
                    }
                }
            """
    }


    private val lineShader = Shader(vertex, fragment, geometry = lineGeometry)
    private val circleShader = Shader(vertex, fragment, geometry = circleGeometry)

    private val oval: Image = loadImage(Resource("res/images/oval.png"))

    var color: Color
        get() = fragment.color.value.toColor()
        set(value) {
            fragment.color.set(value)
        }

    var stroke: Float
        get() = lineGeometry.width.value
        set(value) {
            lineGeometry.width.value = value
            circleGeometry.width.value = value
        }

    var path = ArrayList<Vector>()

    private var floats: FloatArray

    private val vbo: MutableVBO
    private val model: Model

    private var isCircle = false
        set(value) {
            if (value) {
                fragment.mode.value = 1f
            } else {
                fragment.mode.value = 0f
            }
            field = value
        }

    init {
        val initialVertices = 300
        floats = FloatArray(initialVertices*3)
        vbo = VBO.createMutable(initialVertices, positionAttribute)
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
        model.image = oval
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
        vbo.updateBuffer(floats)
    }


}