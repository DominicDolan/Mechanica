package com.mechanica.engine.drawer.shader

import com.mechanica.engine.color.hex
import com.mechanica.engine.shader.attributes.Attribute
import com.mechanica.engine.shader.script.ShaderScript

abstract class DrawerScript : ShaderScript() {
    protected val position by lazy { attribute(Attribute.positionLocation).vec3() }
    protected val textureCoords by lazy { attribute(Attribute.texCoordsLocation).vec2() }

    val color by lazy { uniform.vec4(hex(0x000000FF)) }

    val size by lazy { uniform.vec2(1.0, 1.0) }
    val radius by lazy { uniform.float(0.0f) }

}