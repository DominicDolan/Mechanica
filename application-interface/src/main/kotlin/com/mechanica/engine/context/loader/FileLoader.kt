package com.mechanica.engine.context.loader

import com.mechanica.engine.resources.ExternalResource
import com.mechanica.engine.resources.Resource
import com.mechanica.engine.resources.ResourceDirectory
import java.net.URI
import java.net.URL

interface FileLoader {
    fun resource(path: String): Resource
    fun resource(url: URL): Resource
    fun resource(uri: URI): Resource
    fun externalResource(path: String, createIfAbsent: Boolean): ExternalResource
    fun directory(path: String, recursive: Boolean = false): ResourceDirectory
}