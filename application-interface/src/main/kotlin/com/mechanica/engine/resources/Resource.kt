package com.mechanica.engine.resources

import com.mechanica.engine.context.loader.MechanicaFactory
import com.mechanica.engine.shaders.context.ShaderFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URI
import java.net.URL
import java.nio.ByteBuffer
import kotlin.streams.toList

interface Resource : GenericResource {
    override val lines: List<String>
        get() {
            val reader = BufferedReader(InputStreamReader(stream))
            val lines = reader.lines().toList()
            reader.close()
            return lines
        }
    override val contents: String
        get() {
            val sb = StringBuilder()
            lines.forEach {
                sb.appendLine(it)
            }
            return sb.toString()
        }
    override val buffer: ByteBuffer
        get() {
            val bytes = stream.readAllBytes()
            val buffer = ShaderFactory.bufferFactories.byteBuffer(bytes.size)
            buffer.put(bytes)
            buffer.flip()
            return buffer
        }

    companion object {
        private val factory = MechanicaFactory.fileFactory

        operator fun invoke(path: String): Resource {
            return factory.resource(path)
        }

        fun create(path: String): Resource {
            return factory.resource(path)
        }

        operator fun invoke(url: URL): Resource {
            return factory.resource(url)
        }

        operator fun invoke(uri: URI): Resource {
            return factory.resource(uri)
        }

        fun external(path: String, createIfAbsent: Boolean = true): ExternalResource {
            return factory.externalResource(path, createIfAbsent)
        }

        fun directory(path: String, recursive: Boolean = false): ResourceDirectory {
            return factory.directory(path, recursive)
        }

    }
}

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
