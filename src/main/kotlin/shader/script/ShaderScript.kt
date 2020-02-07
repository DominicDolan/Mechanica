package shader.script

abstract class ShaderScript {
    private val variables = ScriptVariables()
    val script: String by lazy { generateScript() }

    private var programId: Int = 0

    protected val uniform: FirstType = object : FirstType(variables) {
        override val name: String
            get() = "uniform"
    }
    protected val varIn: FirstType = object : FirstType(variables) {
        override val name: String
            get() = "in"
    }
    protected val varOut: FirstType = object : FirstType(variables) {
        override val name: String
            get() = "out"
    }
    protected val varConst: FirstType = object : FirstType(variables) {
        override val name: String
            get() = "const"
    }

    abstract val main: String

    private fun generateScript(): String {
        return variables.scriptHead + "\n" + main.trimIndent() + "\n"
    }

    internal fun loadProgram(programId: Int) {
        this.programId = programId
        variables.setLocations(programId)
    }

    internal fun loadUniforms() {
        variables.loadUniforms()
    }
}