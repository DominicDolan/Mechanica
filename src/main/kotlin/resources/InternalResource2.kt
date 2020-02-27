package resources

import display.Game
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.*

class InternalResource2(private val file: String) : Resource {
    override val path: String
        get() {
            val codeSourcePath = Game::class.java.protectionDomain.codeSource.location.path
            val prefix = ""
            val path1 = "$prefix$codeSourcePath../../../resources/main/$file"
            val path2 = "$prefix$codeSourcePath$file"
            return when {
                File(path1).exists() -> path1
                File(path2).exists() -> path2
                else -> {
                    val p = File(path1).canonicalPath
                    val msg = "File not found at: $p, or at: $path2"
                    throw MissingResourceException(msg, p, file)
                }
            }
        }
    override val stream: InputStream
        get() {
            val file = File(path)
            return FileInputStream(file)
        }

}