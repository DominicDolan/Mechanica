package com.mechanica.engine.drawer.shader

import com.cave.library.color.hex
import com.mechanica.engine.shaders.attributes.Attribute
import com.mechanica.engine.shaders.script.ShaderScript

abstract class DrawerScript : ShaderScript() {
    val position by lazy { attribute(Attribute.positionLocation).vec3() }
    val textureCoords by lazy { attribute(Attribute.texCoordsLocation).vec2() }

    val color by lazy { uniform.vec4(hex(0x000000FF)) }

    val size by lazy { uniform.vec2(1.0, 1.0) }
    val radius by lazy { uniform.float(0.0f) }

}