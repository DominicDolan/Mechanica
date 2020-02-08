package shader.script

class GlobalDeclarations : Declarations("globalVar") {
    public override val uniform: Qualifier
        get() = super.uniform
    public override val varIn: Qualifier
        get() = super.varIn
    public override val varOut: Qualifier
        get() = super.varOut
    public override val varConst: Qualifier
        get() = super.varConst
}