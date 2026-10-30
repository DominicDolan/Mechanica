package com.mechanica.engine.resources

import com.mechanica.engine.context.loader.MechanicaFactory

interface Resource : GenericResource {
    override val lines: List<String>
        get() = readBytes().decodeToString().lineSequence().toList().let {
            // BufferedReader.readLine does not return an extra line after a final newline.
            if (it.lastOrNull() == "" && (it.size > 1 || it[0].isEmpty())) it.dropLast(1) else it
        }
    override val contents: String
        get() = buildString { lines.forEach { appendLine(it) } }
    companion object {
        private val factory = MechanicaFactory.fileFactory

        operator fun invoke(path: String): Resource {
            return factory.resource(path)
        }

        fun create(path: String): Resource {
            return factory.resource(path)
        }

        fun external(path: String, createIfAbsent: Boolean = true): ExternalResource {
            return factory.externalResource(path, createIfAbsent)
        }

        fun directory(path: String, recursive: Boolean = false): ResourceDirectory {
            return factory.directory(path, recursive)
        }

    }
}

class ResourceNotFoundException(path: String, cause: Throwable? = null) : Exception("Resource not found: $path", cause)

interface ExternalResource : Resource {
    fun write(content: String)

    companion object {
        operator fun invoke(path: String) {
            Resource.external(path)
        }

        fun create(path: String) {
            Resource.external(path)
        }
    }
}

abstract class ResourceDirectory {
    abstract val resources: Array<Resource>
    abstract val folders: Array<ResourceDirectory>

    val fileCount: Int
        get() = resources.size

    val folderCount: Int
        get() = folders.size

    abstract val name: String

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
