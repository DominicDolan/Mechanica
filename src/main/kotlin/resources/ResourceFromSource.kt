package resources

import display.Game
import org.lwjgl.BufferUtils
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.JarURLConnection
import java.net.URL
import java.nio.ByteBuffer
import java.util.*

class ResourceFromSource(private val file: String) : Resource {
    override val path: String = getFilePathFromCodeSource()
    override val stream: InputStream get() = getInputStream()
    override val contents: String get() = getContentsAsString()
    override val buffer: ByteBuffer get() = getByteBuffer()

    private var isJar = false

    private fun getFilePathFromCodeSource(): String {
        val codeSourcePath = Game::class.java.protectionDomain.codeSource.location.path
        isJar = codeSourcePath.endsWith(".jar", true)
        val prefix = if (isJar) "jar:file:" else "file:"
        val relativeFilePath = if (file.startsWith("/")) file else "/$file"
        return "$prefix$codeSourcePath!$relativeFilePath"
    }

    private fun getInputStream(): InputStream {
        val path = getFilePathFromCodeSource()
        val inputURL = URL(path)
        val conn = inputURL.openConnection() as JarURLConnection
        return conn.inputStream
    }

    private fun getContentsAsString(): String {
        val sb = StringBuilder()
        val reader = BufferedReader(InputStreamReader(stream))
        reader.lines().forEach {
            sb.appendln(it)
        }
        reader.close()

        return sb.toString()
    }

    private fun getByteBuffer(): ByteBuffer {
        val bytes = stream.readAllBytes()
        val buffer = BufferUtils.createByteBuffer(bytes.size)
        buffer.put(bytes)
        buffer.flip()
        return buffer
    }

}