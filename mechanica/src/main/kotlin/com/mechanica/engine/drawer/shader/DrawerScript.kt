package com.mechanica.engine.drawer.shader

import com.mechanica.engine.color.hex
import com.mechanica.engine.gl.script.ShaderScript
import com.mechanica.engine.gl.vbo.pointer.VBOPointer

abstract class DrawerScript : ShaderScript() {
    protected val position by lazy { attribute(0).vec3("position") }
    protected val textureCoords by lazy { attribute(1).vec2("textureCoords") }

    val color by lazy { uniform.vec4(hex(0x000000FF)) }

    val size by lazy { uniform.vec2(1.0, 1.0) }
    val radius by lazy { uniform.float(0.0f) }

}