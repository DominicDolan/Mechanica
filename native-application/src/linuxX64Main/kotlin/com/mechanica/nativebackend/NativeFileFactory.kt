@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.mechanica.nativebackend

import com.mechanica.engine.context.loader.FileFactory
import com.mechanica.engine.resources.*
import kotlinx.cinterop.*
import platform.posix.*

/** Loads unpacked assets from [assetRoot] and persistence from [persistenceRoot]. */
class NativeFileFactory(private val assetRoot: String, private val persistenceRoot: String) : FileFactory {
    override fun resource(path: String): Resource = NativeResource(resolve(assetRoot, path))
    override fun externalResource(path: String, createIfAbsent: Boolean): ExternalResource =
        NativeExternalResource(resolve(persistenceRoot, path), createIfAbsent)
    override fun directory(path: String, recursive: Boolean): ResourceDirectory =
        NativeResourceDirectory(resolve(assetRoot, path), recursive)
}

private fun resolve(root: String, path: String): String =
    if (path.startsWith('/')) path else root.trimEnd('/') + "/" + path

private open class NativeResource(override val path: String) : Resource {
    override fun readBytes(): ByteArray {
        val file = fopen(path, "rb") ?: run {
            if (errno == ENOENT) throw ResourceNotFoundException(path)
            error("Unable to open resource: $path (errno $errno)")
        }
        try {
            check(fseek(file, 0, SEEK_END) == 0) { "Unable to seek resource: $path" }
            val size = ftell(file)
            check(size >= 0 && size <= Int.MAX_VALUE) { "Invalid resource size: $path" }
            check(fseek(file, 0, SEEK_SET) == 0) { "Unable to seek resource: $path" }
            return ByteArray(size.toInt()).also { bytes ->
                if (bytes.isNotEmpty()) bytes.usePinned {
                    check(fread(it.addressOf(0), 1u, bytes.size.toULong(), file) == bytes.size.toULong()) {
                        "Unable to read resource: $path"
                    }
                }
            }
        } finally { fclose(file) }
    }
}

private class NativeExternalResource(path: String, createIfAbsent: Boolean) : NativeResource(path), ExternalResource {
    init {
        if (createIfAbsent && access(path, F_OK) != 0) {
            ensureParent(path)
            val file = fopen(path, "ab") ?: error("Unable to create resource: $path")
            fclose(file)
        }
    }
    override fun write(content: String) {
        ensureParent(path)
        val file = fopen(path, "wb") ?: error("Unable to write resource: $path")
        try {
            val bytes = content.encodeToByteArray()
            if (bytes.isNotEmpty()) bytes.usePinned {
                check(fwrite(it.addressOf(0), 1u, bytes.size.toULong(), file) == bytes.size.toULong()) {
                    "Unable to write resource: $path"
                }
            }
            check(fflush(file) == 0) { "Unable to flush resource: $path" }
        } finally { fclose(file) }
    }
}

internal fun ensureParent(path: String) {
    val parent = path.substringBeforeLast('/', "")
    if (parent.isEmpty()) return
    var current = if (parent.startsWith('/')) "/" else ""
    for (part in parent.split('/').filter { it.isNotEmpty() }) {
        current = if (current.isEmpty() || current == "/") current + part else "$current/$part"
        if (mkdir(current, 0x1EDu) != 0 && errno != EEXIST) error("Unable to create directory: $current")
    }
}

private class NativeResourceDirectory(
    private val path: String,
    recursive: Boolean,
    scanContents: Boolean = true,
) : ResourceDirectory() {
    override val name = path.trimEnd('/').substringAfterLast('/')
    override val resources: Array<Resource>
    override val folders: Array<ResourceDirectory>

    init {
        val handle = if (scanContents) opendir(path) ?: throw ResourceNotFoundException(path) else null
        val files = mutableListOf<Resource>()
        val directories = mutableListOf<ResourceDirectory>()
        try {
            while (handle != null) {
                val entry = readdir(handle) ?: break
                val name = entry.pointed.d_name.toKString()
                if (name == "." || name == "..") continue
                val child = path.trimEnd('/') + "/" + name
                val isDirectory = memScoped {
                    val info = alloc<stat>()
                    lstat(child, info.ptr) == 0 && (info.st_mode and S_IFMT.toUInt()) == S_IFDIR.toUInt()
                }
                if (isDirectory) {
                    directories += NativeResourceDirectory(child, recursive, recursive)
                } else files += NativeResource(child)
            }
        } finally { if (handle != null) closedir(handle) }
        resources = files.sortedBy { it.path }.toTypedArray()
        folders = directories.sortedBy { it.name }.toTypedArray()
    }
}
