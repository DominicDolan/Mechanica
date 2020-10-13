package com.mechanica.engine.persistence

import com.mechanica.engine.resources.ExternalResource
import com.mechanica.engine.util.StringMapSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonDecodingException
import java.io.FileNotFoundException

class PersistenceMap private constructor(private val path: String, private val map: HashMap<String, Any>) : Map<String, Any> by map {
    constructor(path: String) : this(path, HashMap())

    private val serializer = StringMapSerializer()

    fun store() {
        val json = Json(JsonConfiguration.Stable.copy(prettyPrint = true))
        val string = json.stringify(serializer, map)
        val file = ExternalResource(path, true)
        file.write(string)
    }

    @OptIn(UnstableDefault::class)
    fun populate() {
        val string = try {
            val file = ExternalResource(path, false)
            file.contents
        } catch (ex: FileNotFoundException) {
            "{}"
        }

        try {
            val map = Json.parse(serializer, string)
            map.forEach {
                this.map[it.key] = it.value
            }
        } catch (jde: JsonDecodingException) {
            System.err.println("Unable to read persistence file")
        }

    }

    fun put(name: String, value: Any) {
        map[name] = value
    }

    override fun toString() = map.toString()

}

