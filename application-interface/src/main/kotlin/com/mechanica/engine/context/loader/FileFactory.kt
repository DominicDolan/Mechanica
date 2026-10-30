package com.mechanica.engine.context.loader

import com.mechanica.engine.resources.ExternalResource
import com.mechanica.engine.resources.Resource
import com.mechanica.engine.resources.ResourceDirectory

interface FileFactory {
    fun resource(path: String): Resource
    fun externalResource(path: String, createIfAbsent: Boolean): ExternalResource
    fun directory(path: String, recursive: Boolean = false): ResourceDirectory
}