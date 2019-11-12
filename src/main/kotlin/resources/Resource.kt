package resources

import java.io.InputStream
import java.nio.ByteBuffer

interface Resource {
    val path: String
    val stream: InputStream
    val contents: String
    val buffer: ByteBuffer
}