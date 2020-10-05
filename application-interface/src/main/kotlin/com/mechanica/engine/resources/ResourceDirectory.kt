package com.mechanica.engine.resources

import java.io.FileNotFoundException
import java.net.URI
import java.nio.file.*
import java.util.stream.Stream

class ResourceDirectory private constructor(directory: String, recursive: Boolean = false, fs: FileSystem?) {
    val resources: Array<Resource>
    val folders: Array<ResourceDirectory>

    val fileCount: Int
        get() = resources.size

    val folderCount: Int
        get() = folders.size

    val name: String

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
                    directoryList.add(ResourceDirectory("$pathString/$relative", recursive, fileSystem))
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

    fun getFile(index: Int) = resources[index]
    fun getFolder(index: Int) = folders[index]

    inline fun forEachFile(operation: (Resource) -> Unit) {
        for (i in 0 until fileCount) {
            operation(getFile(i))
        }
    }

    inline fun forEachFolder(operation: (ResourceDirectory) -> Unit) {
        for (i in 0 until folderCount) {
            operation(getFolder(i))
        }
    }

}