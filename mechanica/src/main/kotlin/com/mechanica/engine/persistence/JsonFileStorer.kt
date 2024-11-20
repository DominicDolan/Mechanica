package com.mechanica.engine.persistence

import com.mechanica.engine.resources.Resource
import java.io.FileNotFoundException


class JsonFileStorer(private val path: String) : JsonStorer {
    override fun getJson(): String {
        return try {
            val file = Resource.external(path, false)
            file.contents
        } catch (ex: FileNotFoundException) {
            "{}"
        }
    }

    override fun storeJson(json: String) {
        val file = Resource.external(path, true)
        file.write(json)
    }

}