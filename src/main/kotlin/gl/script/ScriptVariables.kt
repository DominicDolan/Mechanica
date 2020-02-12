package gl.script

import gl.glvars.GLVar

class ScriptVariables(private val placeHolder: String): Iterable<GLVar<*>> {
    private val variables = ArrayList<GLVar<*>>()

    val scriptHead: String
        get() = "#version 400 core\n\n$declarations"

    val declarations: String
        get() {
            val sb = StringBuilder()
            for (v in variables) {
                sb.append(v.declaration)
            }
            return sb.toString()
        }
    fun getNextName(): String {
        return "$placeHolder${variables.size}"
    }

    fun addVariable(v: GLVar<*>) {
        if (variables.containsName(v.name)) throw IllegalStateException("Two variables have been declared with the name: ${v.name}")
        variables.add(v)
    }

    private fun ArrayList<GLVar<*>>.containsName(name: String): Boolean {
        for (v in this) {
            if (v.name == name) return true
        }
        return false
    }

    override fun iterator(): Iterator<GLVar<*>> {
        return variables.iterator()
    }

}