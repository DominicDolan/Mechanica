package resources

import java.io.File
import java.io.InputStream
import java.net.JarURLConnection
import java.net.URL
import java.util.*

class JarResource(private val file: String, private val referenceClass: Class<*>) : Resource {
    override val path: String
    override val stream: InputStream

    init {
        val codeSourcePath = referenceClass.protectionDomain.codeSource.location.path
        val prefix = "jar:file:"
        val relativeFilePath = if (file.startsWith("/")) file else "/$file"
        val path = "$prefix$codeSourcePath!$relativeFilePath"
        if (URL(path).file.isEmpty() || !codeSourcePath.contains("jar")) {
            val msg = "File not found at: $path"
            throw MissingResourceException(msg, path, file)
        }
        this.path = path

        val inputURL = URL(path)
        val conn = inputURL.openConnection() as JarURLConnection
        stream = conn.inputStream
    }
}