package resources

import org.lwjgl.BufferUtils
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.ByteBuffer

interface Resource {
    val path: String
    val stream: InputStream
    val contents: String
        get() {
            val sb = StringBuilder()
            val reader = BufferedReader(InputStreamReader(stream))
            reader.lines().forEach {
                sb.appendln(it)
            }
            reader.close()

            return sb.toString()
        }
    val buffer: ByteBuffer
        get() {
            val bytes = stream.readAllBytes()
            val buffer = BufferUtils.createByteBuffer(bytes.size)
            buffer.put(bytes)
            buffer.flip()
            return buffer
        }

}