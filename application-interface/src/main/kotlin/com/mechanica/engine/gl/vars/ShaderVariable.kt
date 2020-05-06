package com.mechanica.engine.gl.vars

import com.mechanica.engine.gl.qualifiers.Qualifier

interface ShaderVariable {
    val name: String
    val qualifier: Qualifier
    val type: String

    val declaration: String
        get() = "$qualifier $type $name; \n"
}