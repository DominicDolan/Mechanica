package gl.script

import gl.vbo.pointer.AttributePointer

class GlobalShaderDeclarations : ShaderDeclarations("globalVar") {
    public override val uniform: Qualifier
        get() = super.uniform
    public override fun attribute(pointer: AttributePointer): Qualifier {
        return super.attribute(pointer)
    }
}