package resources

import display.Game
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.net.URI

class ExternalResource(filePath: String, createIfAbsent: Boolean = false) : Resource {
    private val file: File? = if (Game.isJar) File(filePath) else null
    private val resource: Resource?

    override val path: String
        get() = file?.path ?: resource?.path ?: ""
    override val stream: InputStream
        get() {
            return when {
                this.file != null -> {
                    FileInputStream(file)
                }
                resource != null -> {
                    resource.stream
                }
                else -> {
                    ByteArrayInputStream(byteArrayOf())
                }
            }
        }

    init {
        if (createIfAbsent) {
            if (file != null && !file.exists()) {
                file.createNewFile()
            }
        }
        resource = if (!Game.isJar) getResource(filePath, createIfAbsent) else null
    }

    private fun getResource(filePath: String, createIfAbsent: Boolean): Resource {
        return if (createIfAbsent) {
             try {
                Resource(filePath)
            } catch (ex: java.lang.IllegalStateException) {
                val uri: URI = this::class.java.getResource("/res").toURI()
                File("${uri.path}/../$filePath").createNewFile()

                Resource(filePath)
            }
        } else Resource(filePath)
    }
}