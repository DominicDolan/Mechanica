package gl.script

import gl.glvars.GLVar
import gl.vbo.pointer.AttributePointer
import gl.vbo.pointer.VBOPointer
import util.colors.hex

abstract class ShaderDeclarations(variableName: String = "autoVal") {
    private val variables = ScriptVariables(variableName)
    protected val iterator: Iterator<GLVar<*>>
        get() = variables.iterator()

    val declarations: String
        get() = variables.declarations

    protected open val uniform: Qualifier = qualifier("uniform")
    protected open fun attribute(pointer: AttributePointer) = qualifier("layout (location=${pointer.index}) in")

    protected val position by lazy { attribute(VBOPointer.position).vec3("position") }
    protected val textureCoords by lazy { attribute(VBOPointer.texCoords).vec2("textureCoords") }

    val color by lazy { uniform.vec4(hex(0x000000FF)) }

    fun qualifier(name: String) = object : Qualifier(variables) {
        override val qualifierName: String
            get() = name
    }

    fun addOther(body: String) {
        variables.addFunction(body)
    }

    companion object {
        var version = "430"
        val header: String
            get() = "#version $version core\n"

        private val globals = GlobalShaderDeclarations()
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

        fun addGlobal(adder: GlobalShaderDeclarations.() -> Unit) {
            adder(globals)
        }

    }
}