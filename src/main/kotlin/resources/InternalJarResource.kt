package resources

import display.Game
import java.io.InputStream
import java.net.JarURLConnection
import java.net.URL
import java.nio.ByteBuffer

class InternalJarResource(private val file: String) : Resource {
    override val path: String
        get() {
            val codeSourcePath = Game::class.java.protectionDomain.codeSource.location.path
            val prefix = "jar:file:"
            val relativeFilePath = if (file.startsWith("/")) file else "/$file"
            return "$prefix$codeSourcePath!$relativeFilePath"
        }
    override val stream: InputStream
        get() {
            val inputURL = URL(path)
            val conn = inputURL.openConnection() as JarURLConnection
            return conn.inputStream
        }
}