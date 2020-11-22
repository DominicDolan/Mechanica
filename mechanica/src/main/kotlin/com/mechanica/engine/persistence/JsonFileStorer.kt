package com.mechanica.engine.persistence

import com.mechanica.engine.resources.ExternalResource
import java.io.FileNotFoundException


class JsonFileStorer(private val path: String) : JsonStorer {
    override fun getJson(): String {
        return try {
            val file = ExternalResource(path, false)
            file.contents
        } catch (ex: FileNotFoundException) {
            "{}"
        }
    }

    override fun storeJson(json: String) {
        val file = ExternalResource(path, true)
        file.write(json)
    }

}