package com.mechanica.engine.resources

import java.net.URI
import java.nio.file.*
import java.util.stream.Stream


class ResourceDirectory(directory: String): Iterable<Resource> {
    private val resources: Array<Resource>
    val fileCount: Int
        get() = resources.size

    init {
        val list = ArrayList<Resource>()

        val uri: URI = this::class.java.getResource(directory).toURI()

        var fileSystem: FileSystem? = null

        val path = if (uri.scheme == "jar") {
            fileSystem = FileSystems.newFileSystem(uri, emptyMap<String, Any>())
            fileSystem.getPath(directory)
        } else {
            Paths.get(uri)
        }

        val walk: Stream<Path> = Files.walk(path, 1)
        for (e in walk) {
            if (!Files.isDirectory(e))
                list.add(Resource(e.toUri()))
        }

        fileSystem?.close()
        list.sortBy { it.path }
        resources = list.toTypedArray()
    }

    override fun iterator() = resources.iterator()


}