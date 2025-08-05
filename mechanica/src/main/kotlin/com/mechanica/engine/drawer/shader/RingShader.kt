package com.mechanica.engine.drawer.shader

class RingShader : DrawerShader() {
    override val vertex = object : DrawerScript() {
        //language=GLSL
        override val main: String = """
            out vec2 pos;
            void main(void) {
                pos = $position.xy;
                gl_Position = matrices(vec4($position, 1.0));
            }
        """

    }
    private val _fragment = object : DrawerScript() {

        val thickness = uniform.float(0.1f)  // Controls the ring thickness

        //language=GLSL
        override val main: String = """
            
            out vec4 fragColor;
            in vec2 pos;
            
            void main(void) {
                vec2 center = pos - vec2(0.5);
                
                float dist = length(center);
                
                float outerRadius = 0.5;
                float innerRadius = outerRadius - $thickness/$size.x;
                
                float edge = 0.05/$size.x;
                float outer = 1.0 - smoothstep(outerRadius - edge, outerRadius, dist);
                float inner = smoothstep(innerRadius - edge, innerRadius, dist);
                
                float ring = outer * inner;
                
                fragColor = vec4($color.rgb, $color.a*ring);

            }
        """
    }

    override val fragment: DrawerScript
        get() = _fragment

    var thickness: Float
        get() = _fragment.thickness.value
        set(value) {
            _fragment.thickness.value = value
        }
}
