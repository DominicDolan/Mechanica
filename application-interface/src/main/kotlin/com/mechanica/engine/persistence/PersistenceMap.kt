package com.mechanica.engine.persistence

import com.mechanica.engine.util.StringMapSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonDecodingException

class PersistenceMap private constructor(private val jsonStorer: JsonStorer, private val map: HashMap<String, Any>) : Map<String, Any> by map {
    constructor(jsonStorer: JsonStorer) : this(jsonStorer, HashMap())

    private val serializer = StringMapSerializer()

    fun store() {
        val json = Json(JsonConfiguration.Stable.copy(prettyPrint = true))
        val string = json.stringify(serializer, map)
        jsonStorer.storeJson(string)
    }

    @OptIn(UnstableDefault::class)
    fun populate() {
        val string = jsonStorer.getJson()

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

