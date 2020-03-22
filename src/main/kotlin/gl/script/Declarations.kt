package gl.script

import gl.vbo.pointer.AttributePointer
import gl.glvars.GLVar
import gl.vbo.pointer.VBOPointer

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
    protected open fun attribute(pointer: AttributePointer) = qualifier("layout (location=${pointer.index}) in")

    protected val position by lazy { attribute(VBOPointer.position).vec3("position") }
    protected val textureCoords by lazy { attribute(VBOPointer.texCoords).vec2("textureCoords") }

    fun qualifier(name: String) = object : Qualifier(variables) {
        override val qualifierName: String
            get() = name
    }

    companion object {
        var version = "430"
        val header: String
            get() = "#version $version core\n"

        private val globals = GlobalDeclarations()
        val iterator: Iterator<GLVar<*>>
            get() = globals.iterator

        val globalDeclarations: String
            get() = globals.declarations

        private val globalMethods = ArrayList<String>()
        internal fun globalMethodDeclarations(sb: StringBuilder) {
            for (method in globalMethods) {
                sb.appendln()
                sb.appendln(method)
            }
        }

        fun function(function: String) {
            globalMethods.add(function.trimIndent())
        }

        val projection = globals.uniform.mat4("projection")
        val transformation = globals.uniform.mat4("transformation")
        val view = globals.uniform.mat4("view")

        fun addGlobal(adder: GlobalDeclarations.() -> Unit) {
            adder(globals)
        }

    }
}