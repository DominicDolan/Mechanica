package drawer.shader

import gl.script.ShaderScript
import gl.vbo.pointer.VBOPointer
import util.colors.hex

abstract class DrawerScript : ShaderScript() {
    protected val position by lazy { attribute(VBOPointer.position).vec3("position") }
    protected val textureCoords by lazy { attribute(VBOPointer.texCoords).vec2("textureCoords") }

    val color by lazy { uniform.vec4(hex(0x000000FF)) }

    val size by lazy { uniform.vec2(1.0, 1.0) }
    val radius by lazy { uniform.float(0.0f) }

}