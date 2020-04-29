package com.mechanica.engine.gl.script

import com.mechanica.engine.gl.script.Qualifier
import com.mechanica.engine.gl.script.ShaderDeclarations
import com.mechanica.engine.gl.vbo.pointer.AttributePointer

class GlobalShaderDeclarations : ShaderDeclarations("globalVar") {
    public override val uniform: Qualifier
        get() = super.uniform
    public override fun attribute(pointer: AttributePointer): Qualifier {
        return super.attribute(pointer)
    }
}