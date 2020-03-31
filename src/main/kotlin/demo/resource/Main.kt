package demo.resource

import drawer.Drawer
import state.State
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import java.nio.file.Paths

fun main() {


}

private class ResourceGetter {
    fun get() : URL {
        return ResourceGetter::class.java.getResource("ResourceGetter.class")
    }
    fun get(clazz: Class<*>) : URL {
        val stack = Exception().stackTrace
        println(stack.last().className)
        println(Class.forName(stack.last().className).simpleName)
        val className = clazz.simpleName
        println("Name: $className")
        return clazz.getResource("$className.class")
    }
}

fun isJar(): Boolean {
    return ResourceGetter().get().protocol == "jar"
}

fun isJar(clazz: Class<*>): Boolean {
    return ResourceGetter().get(clazz).protocol == "jar"
}

fun testLocation() {
    println(File(".").absolutePath)
}

private fun isPathAbsolute(file: String): Boolean {
//    return File(file).isAbsolute
    return Paths.get(file).isAbsolute
}

private fun getURL(file: String): URL? {
    val systemResources = ClassLoader.getSystemResources(file)
    return if (systemResources != null && systemResources.hasMoreElements())
                systemResources.nextElement()
            else null
}

private fun getStream(url: URL): InputStream {
    val conn = url.openConnection()
    return conn.getInputStream()
}

private fun getContentsFromStream(stream: InputStream): String {
    val sb = StringBuilder()
    val reader = BufferedReader(InputStreamReader(stream))
    reader.lines().forEach {
        sb.appendln(it)
    }
    reader.close()

    return sb.toString()
}


private class StartMain : State() {
    override fun update(delta: Double) {

    }

    override fun render(draw: Drawer) {

    }

}
