package resources

import display.Game
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStream
import java.nio.ByteBuffer

class InternalFileResource(private val file: String) : Resource {
    override val path: String
        get() {
            val codeSourcePath = Game::class.java.protectionDomain.codeSource.location.path
            val prefix = ""
            val path1 = "$prefix$codeSourcePath../../../resources/main/$file"
            val path2 = "$prefix$codeSourcePath$file"
            return when {
                File(path1).exists() -> path1
                File(path2).exists() -> path2
                else -> throw FileNotFoundException("File not found at: ${File(path1).canonicalPath}, or at: $path2")
            }
        }
    override val stream: InputStream
        get() {
            val file = File(path)
            return FileInputStream(file)
        }
}