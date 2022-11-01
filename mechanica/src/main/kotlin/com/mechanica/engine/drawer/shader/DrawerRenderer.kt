package com.mechanica.engine.drawer.shader

import com.cave.library.color.Color
import com.cave.library.color.toColor
import com.cave.library.matrix.mat4.Matrix4
import com.cave.library.vector.vec2.MutableVector2
import com.mechanica.engine.drawer.state.DrawState
import com.mechanica.engine.shaders.models.Model

class DrawerRenderer {

    private val vertex = object : DrawerScript() {
        //language=GLSL
        override val main: String =
                """
                layout (binding=0) uniform sampler2D samp;
                out vec2 pos;
                out vec2 tc;
                void main(void) {
                    pos = $position.xy;
                    tc = $textureCoords;
                    gl_Position = matrices(vec4($position, 1.0));
                }
                """

    }

    private val fragment = object : DrawerScript() {

        val blend = uniform.float(0f)
        val alphaBlend = uniform.float(0f)
        val colorPassthrough = uniform.float(0f)

        //language=GLSL
        override val main: String = """
                layout (binding=0) uniform sampler2D samp;
                out vec4 fragColor;
                in vec2 pos;
                in vec2 tc;
                
                void main(void) {
                    vec4 inColor;
                    if ($blend > 0.0 || $alphaBlend > 0.0) {
                        vec4 texColor = texture(samp, tc);
                        inColor = vec4(mix($color.rgb, texColor.rgb, $blend), $color.a*texColor.a);
                    } else {
                        inColor = $color;
                    }
                    
                    if ($colorPassthrough == 0.0) {
                        vec2 st = pos - vec2(0.5);
                        
                        float height = abs($size.y);
                        float width = abs($size.x);
                        
                        st = vec2(st.x*width, st.y*height);
                        
                        float smoothStroke = 3.0*pixelSize;
                        float strokeShift = 0.7;
                        float radius = max($radius, smoothStroke*strokeShift);
    
                        vec2 relative = abs(st) - vec2(0.5*width - radius, 0.5*height - radius);
                        float distance = length(max(relative, 0.0));
                        
                        float smoothStart = radius - smoothStroke*strokeShift;
                        float smoothEnd = radius + smoothStroke*(1.0 - strokeShift);
                        
                        float alpha = 1.0 - smoothstep(smoothStart, smoothEnd, distance);
                        fragColor = vec4(inColor.rgb, inColor.a*alpha);
                    } else {
                        fragColor = inColor;
                    }
                }
            """

    }

    val shader: DrawerShader by lazy { DrawerShader.create(vertex, fragment) }

    var color: Color
        get() = fragment.color.value.toColor()
        set(value) {
            fragment.color.set(value)
        }

    fun rgba(r: Double, g: Double, b: Double, a: Double) {
        fragment.color.set(r, g, b, a)
    }

    var radius: Float
        get() = fragment.radius.value
        set(value) {
            fragment.radius.value = value
        }

    var blend: Float
        get() = fragment.blend.value
        set(value) {
            fragment.blend.value = value
        }

    var alphaBlend: Float
        get() = fragment.alphaBlend.value
        set(value) {
            fragment.alphaBlend.value = value
        }

    var colorPassthrough: Boolean
        get() = fragment.colorPassthrough.value == 1f
        set(value) {
            fragment.colorPassthrough.value =
                    if (value) 1f else 0f
        }

    val size: MutableVector2 = fragment.size.value

    var strokeWidth = 0.0

    fun render(model: Model, transformation: Matrix4, projection: Matrix4, view: Matrix4) {
        shader.render(model, transformation, projection, view)
    }

    fun render(state: DrawState, blend: Float, alphaBlend: Float, colorPassthrough: Boolean) {
        fragment.blend.value = blend
        fragment.alphaBlend.value = alphaBlend
        fragment.colorPassthrough.value = if (colorPassthrough) 1f else 0f

        fragment.radius.value = state.shader.radius.value.toFloat()
        fragment.size.set(state.shader.cornerSize)

        state.color.assignColorsToFragment(fragment)

        val transformation = state.transformation.getTransformationMatrix()
        shader.render(state.shader.model.variable, transformation, state.projectionMatrix.variable, state.viewMatrix.variable)

        state.reset()
    }



    fun rewind() {
        colorPassthrough = false
        blend = 0f
        alphaBlend = 0f
    }
}