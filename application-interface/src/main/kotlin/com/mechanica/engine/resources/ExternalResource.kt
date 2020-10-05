package com.mechanica.engine.resources

import java.io.*

class ExternalResource(filePath: String, createIfAbsent: Boolean = false) : Resource {
    private val absoluteFile: File
    override val path: String
        get() = absoluteFile.path
    override val stream: InputStream
        get() = FileInputStream(absoluteFile)

    init {
        val path =if (File(filePath).isAbsolute) filePath
            else (System.getenv("APPDATA") + "/Mechanica/" + filePath).replace("/", System.getProperty("file.separator"))

        absoluteFile = getFile(path, createIfAbsent)
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

    fun write(content: String) {
        try {
            absoluteFile.parentFile.mkdirs()
            val fw = FileWriter(absoluteFile)
            val bw = BufferedWriter(fw)
            bw.write(content)
            bw.close()
        } catch (e: IOException) {
            System.err.println("Error handling writing file for: $absoluteFile")
        } catch (e: FileNotFoundException) {
            System.err.println("Error handling writing file for: $absoluteFile, it is possible that access was denied")
        }
    }
}