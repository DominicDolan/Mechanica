package demo.resource

import drawer.Drawer
import resources.Res
import resources.Resource
import resources.ResourceDirectory
import resources.StandardResource
import state.State
import java.io.*
import java.lang.Exception
import java.net.URL
import java.nio.file.Paths

fun main() {
    val file = "res/text/mechanica.txt"
    val resource = Resource(file)

    val directory = "/res/text"
    val dir = ResourceDirectory(directory)

    for (r in dir) {
        println(r.contents)
    }


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
