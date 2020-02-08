package shader.script

import shader.glvars.GLVar

abstract class Declarations(variableName: String = "autoVal") {
    private val variables = ScriptVariables(variableName)
    protected val iterator: Iterator<GLVar<*>>
        get() = variables.iterator()

    val declarations: String
        get() = variables.declarations

    protected open val uniform: Qualifier = qualifier("uniform")
    protected open val varIn: Qualifier = qualifier("in")
    protected open val varOut: Qualifier = qualifier("out")
    protected open val varConst: Qualifier = qualifier("const")

    fun qualifier(name: String) = object : Qualifier(variables) {
        override val name: String
            get() = name
    }


    companion object {
        var version = "430"
        val header: String
            get() = "#version $version core\n"

        private val globals = GlobalDeclarations()

        val globalDeclarations: String
            get() = globals.declarations

        val position = globals.qualifier("layout (location=0) in").type("vec3", "position", floatArrayOf(0f, 0f, 0f))
        val textureCoords = globals.qualifier("layout (location=1) in").type("vec2", "textureCoords", floatArrayOf(0f, 0f))

        fun addGlobal(adder: GlobalDeclarations.() -> Unit) {
            adder(globals)
        }

    }
}