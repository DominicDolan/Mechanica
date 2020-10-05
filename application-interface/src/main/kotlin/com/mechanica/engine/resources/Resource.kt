package com.mechanica.engine.resources

import com.mechanica.engine.context.loader.GLLoader
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.InputStream
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
            val buffer = GLLoader.bufferLoader.byteBuffer(bytes.size)
            buffer.put(bytes)
            buffer.flip()
            return buffer
        }

    companion object {

        operator fun invoke(file: String): Resource {
            val fileForURL = file.replace("\\", "/")
            val url = getResourceURL(fileForURL) ?: throw FileNotFoundException("Resource not found at $fileForURL")
            return ResourceImpl(url)
        }

        operator fun invoke(url: URL): Resource {
            return ResourceImpl(url)
        }

        operator fun invoke(uri: URI): Resource {
            return ResourceImpl(uri.toURL())
        }

        fun external(file: String): Resource {
            return ExternalResource(file)
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
}