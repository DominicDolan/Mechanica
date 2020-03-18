package resources

import display.Game
import java.io.*
import java.net.URI
import java.nio.file.Paths
import kotlin.system.exitProcess

class ExternalResource(filePath: String, createIfAbsent: Boolean = false) : Resource {
    private val absoluteFile: File

    private val fileForJar: File?
    private val resource: Resource?

    override val path: String
        get() = absoluteFile.path
    override val stream: InputStream
        get() {
            return when {
                this.fileForJar != null -> {
                    FileInputStream(fileForJar)
                }
                resource != null -> {
                    resource.stream
                }
                else -> {
                    ByteArrayInputStream(byteArrayOf())
                }
            }
        }

    fun write(content: String) {
        try {
            val fw = FileWriter(absoluteFile.absoluteFile)
            val bw = BufferedWriter(fw)
            bw.write(content)
            bw.close()
        } catch (e: IOException) {
            e.printStackTrace()
            exitProcess(-1)
        }
    }

    init {
        fileForJar = if (Game.isJar) getFile(filePath, createIfAbsent) else null
        resource = if (!Game.isJar) getResource(filePath, createIfAbsent) else null

        absoluteFile = when {
            fileForJar != null -> fileForJar
            resource != null -> File("${getResourceLocation()}/$filePath")
            else -> File("")
        }
    }

    private fun getFile(filePath: String, createIfAbsent: Boolean): File {
        val file = File(filePath)
        if (createIfAbsent && !file.exists()) {
            if (!file.parentFile.exists()) {
                file.parentFile.mkdirs()
            }
            file.createNewFile()
        }
        return file
    }

    private fun getResource(filePath: String, createIfAbsent: Boolean): Resource {
        return if (createIfAbsent) {
            try {
                Resource(filePath)
            } catch (ex: java.lang.IllegalStateException) {
                val newFile = File("${getResourceLocation()}$filePath")
                newFile.parentFile.mkdirs()
                newFile.createNewFile()

                Resource(filePath)
            }
        } else Resource(filePath)
    }

    private fun getResourceLocation(): String {
        val uri: URI = this::class.java.getResource("/res").toURI()
        return Paths.get(uri).parent.toString()
    }
}