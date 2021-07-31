package com.mechanica.engine.drawer.shader

import com.cave.library.color.Color
import com.cave.library.color.toColor
import com.cave.library.matrix.mat4.Matrix4
import com.cave.library.vector.vec2.Vector2
import com.mechanica.engine.context.loader.MechanicaFactory
import com.mechanica.engine.shaders.attributes.AttributeArray
import com.mechanica.engine.shaders.attributes.FloatAttributeArray
import com.mechanica.engine.shaders.models.Model
import com.mechanica.engine.util.VectorArray
import com.mechanica.engine.util.extensions.fill
import org.intellij.lang.annotations.Language

class PathRenderer() {

    private val vertex = object : DrawerScript() {

        //language=GLSL
        override val main: String =
                """
                void main(void) {
                    gl_Position = vec4($position, 1.0);
                }
                """

    }

    private val lineGeometry = object : DrawerScript() {

        val width = uniform.float(1f)

        @Language("GLSL")
        override val main: String = """
            layout (lines) in;
            layout (triangle_strip, max_vertices = 4) out;
                       
            out GS_OUT
            {
                vec2 v_TexCoord;
            } gs_out;
             
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

    private val circleGeometry = object : DrawerScript() {

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


    private val fragment = object : DrawerScript() {

        val mode = uniform.float(0f)

        //language=GLSL
        override val main: String = """
                out vec4 fragColor;
                in GS_OUT
                {
                    vec2 v_TexCoord;
                } fs_in;
                                
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

    private val lineShader = DrawerShader.create(vertex, fragment, geometry = lineGeometry) {
        prepareStencil()

        drawLineStrip.arrays(it)

        MechanicaFactory.miscFactory.stencilFunction()
    }
    private val circleShader = DrawerShader.create(vertex, fragment, geometry = circleGeometry) {
        prepareStencil()

        drawPoints.arrays(it)

        MechanicaFactory.miscFactory.enableAlphaBlending()

        MechanicaFactory.miscFactory.stencilFunction()
    }

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

    private var floats: FloatArray

    private val vbo: FloatAttributeArray
    private val model: Model

    init {
        val initialVertices = 300
        floats = FloatArray(initialVertices*3)
        vbo = AttributeArray.createPositionArray(floats)
        model = Model(vbo)
    }

    fun render(transformation: Matrix4) {
        MechanicaFactory.miscFactory.clearStencil()
        fragment.mode.value = 0f
        lineShader.render(this.model, transformation)
        fragment.mode.value = 1f
        circleShader.render(this.model, transformation)
    }

    fun fillFloats(path: List<Vector2>, count: Int = path.size) {
        checkFloats(count)
        floats.fill(path, end = count)
        vbo.value = floats
        vbo.updateBuffer()
    }

    fun fillFloats(path: Array<out Vector2>, count: Int = path.size) {
        checkFloats(count)
        floats.fill(path, end = count)
        vbo.value = floats
        vbo.updateBuffer()
    }

    fun fillFloats(path: VectorArray, count: Int = path.size) {
        checkFloats(count)
        floats.fill(path, end = count)
    }

    private fun checkFloats(pathSize: Int) {
        model.vertexCount = pathSize
        if (pathSize*3 >= floats.size) {
            floats = FloatArray(pathSize*3*2)
        }
    }

    private fun prepareStencil() {
        MechanicaFactory.miscFactory.prepareStencilForPath()
    }


}