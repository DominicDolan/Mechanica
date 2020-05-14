package com.mechanica.engine.drawer.shader

import com.mechanica.engine.color.Color
import com.mechanica.engine.color.toColor
import com.mechanica.engine.models.Model
import com.mechanica.engine.unit.vector.DynamicVector
import org.joml.Matrix4f

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
                        inColor = vec4(mix($color.rgb, texColor.rgb, $blend), mix($color.a, texColor.a, $alphaBlend));
                    } else {
                        inColor = $color;
                    }
                    
                    if ($colorPassthrough == 0f) {
                        vec2 st = pos - vec2(0.5);
                        
                        float height = $size.y;
                        float width = $size.x;
                        
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

    private val shader: DrawerShader by lazy { DrawerShader(vertex, fragment) }

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

    val size: DynamicVector = fragment.size.value

    var strokeWidth = 0.0

    fun render(model: Model, transformation: Matrix4f, view: Matrix4f, projection: Matrix4f) {
        shader.render(model, transformation, projection, view)
    }

    fun rewind() {
        colorPassthrough = false
        blend = 0f
        alphaBlend = 0f
    }
}