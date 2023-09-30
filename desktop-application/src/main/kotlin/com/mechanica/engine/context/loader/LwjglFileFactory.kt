package com.mechanica.engine.context.loader

import com.mechanica.engine.resources.ExternalResource
import com.mechanica.engine.resources.Resource
import com.mechanica.engine.resources.ResourceDirectory
import java.io.*
import java.net.URI
import java.net.URL
import java.nio.file.*
import java.util.stream.Stream

class LwjglFileFactory : FileFactory {
    override fun resource(path: String): Resource {
        val fileForURL = path.replace("\\", "/")
        val url = getResourceURL("./" + fileForURL) ?: throw FileNotFoundException("Resource not found at $fileForURL")
        return ResourceImpl(url)
    }

    override fun resource(url: URL): Resource {
        return ResourceImpl(url)
    }

    override fun resource(uri: URI): Resource {
        return ResourceImpl(uri.toURL())
    }

    override fun externalResource(path: String, createIfAbsent: Boolean): ExternalResource {
        return LwjglExternalResource(path, createIfAbsent)
    }

    override fun directory(path: String, recursive: Boolean): ResourceDirectory {
        return LwjglResourceDirectory(path, recursive)
    }

    private fun getResourceURL(file: String): URL? {
        val systemResources = ClassLoader.getSystemResources(file)
        return if (systemResources != null && systemResources.hasMoreElements())
            systemResources.nextElement()
        else null
    }

    private class ResourceImpl(private val url: URL) : Resource {
        private val protocol = url.protocol
        override val path: String
            get() {
                return if (protocol == "jar") {
                    URL(url.path).path
                } else {
                    url.path
                }
            }
        override val stream: InputStream
            get() {
                val conn = url.openConnection()
                return conn.getInputStream()
            }

    }
}

class LwjglExternalResource(filePath: String, createIfAbsent: Boolean = false) : ExternalResource {
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

    override fun write(content: String) {
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


class LwjglResourceDirectory private constructor(directory: String, recursive: Boolean = false, fs: FileSystem?) : ResourceDirectory() {
    override val resources: Array<Resource>
    override val folders: Array<ResourceDirectory>

    override val name: String

    constructor(directory: String, recursive: Boolean = false) : this(directory, recursive, null)

    init {
        val resourceList = ArrayList<Resource>()
        val directoryList = ArrayList<ResourceDirectory>()

        val pathString = directory.removeSuffix("\\").removeSuffix("/")
        val uri: URI = this::class.java.getResource(pathString)?.toURI() ?: throw FileNotFoundException("No file or directory found at $pathString")

        var fileSystem: FileSystem? = null

        val path = if (uri.scheme == "jar") {
            fileSystem = fs ?: FileSystems.newFileSystem(uri, emptyMap<String, Any>())
            fileSystem?.getPath(pathString) ?: throw FileNotFoundException("Error opening filesystem for file: $pathString")
        } else {
            Paths.get(uri)
        }

        name = path.fileName.toString()

        val walk: Stream<Path> = Files.walk(path, 1)
        for (e in walk) {
            if (!Files.isDirectory(e)) {
                resourceList.add(Resource(e.toUri()))
            } else if (Files.isDirectory(e)) {
                val relative = path.relativize(e).toString()
                if (relative.isNotEmpty() && relative != "." && relative != "..") {
                    directoryList.add(LwjglResourceDirectory("$pathString/$relative", recursive, fileSystem))
                }
            }
        }

        if (fs == null) {
            fileSystem?.close()
        }

        resourceList.sortBy { it.path }
        resources = resourceList.toTypedArray()

        folders = directoryList.toTypedArray()
    }

}