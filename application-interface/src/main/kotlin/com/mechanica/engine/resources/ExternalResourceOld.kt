package com.mechanica.engine.resources

import com.mechanica.engine.util.calculateIsJar
import java.io.*
import java.net.URI
import java.nio.file.Paths

class ExternalResourceOld(filePath: String, createIfAbsent: Boolean = false) : Resource {
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

    init {
        val sep = System.getProperty("file.separator")
        val path = "E:\\Downloads" + sep + filePath.replace("/", sep)//System.getenv("APPDATA") + sep + "Mechanica" + sep + filePath.replace("/", sep)
        println(path)
        val isJar = calculateIsJar()
        fileForJar = if (isJar) getFile(path, createIfAbsent) else null
        resource = if (!isJar) getResource(path, createIfAbsent) else null

        absoluteFile = when {
            fileForJar != null -> fileForJar
            resource != null -> File("${getResourceLocation()}/$path")
            else -> File("")
        }
    }

    fun write(content: String) {
        try {
            absoluteFile.absoluteFile.parentFile.mkdirs()
            val fw = FileWriter(absoluteFile.absoluteFile)
            val bw = BufferedWriter(fw)
            bw.write(content)
            bw.close()
        } catch (e: IOException) {
            System.err.println("Error handling writing file for: $absoluteFile")
        } catch (e: FileNotFoundException) {
            System.err.println("Error handling writing file for: $absoluteFile, it is possible that access was denied")
        }
    }

    private fun getFile(filePath: String, createIfAbsent: Boolean): File {
        println("getting file, path: $path")
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
            } catch (ex: FileNotFoundException) {
                val newFile = File(filePath)
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