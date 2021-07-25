package com.mechanica.engine.persistence

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

class PersistenceMap private constructor(private val jsonStorer: JsonStorer, private val map: HashMap<String, Any>) : Map<String, Any> by map {
    constructor(jsonStorer: JsonStorer) : this(jsonStorer, HashMap())

    fun store() {
        val json = JsonConverter.mapToJson(map).toString()
        jsonStorer.storeJson(json)
    }

    fun populate() {
        val jsonString = jsonStorer.getJson()
        val json = Json.parseToJsonElement(jsonString)
        if (json is JsonObject) {
            JsonConverter.mapFromJson(json).forEach {
                this.map[it.key] = it.value
            }
        }
    }

    fun put(name: String, value: Any) {
        map[name] = value
    }

    fun clear() = map.clear()

    override fun toString() = map.toString()

}

