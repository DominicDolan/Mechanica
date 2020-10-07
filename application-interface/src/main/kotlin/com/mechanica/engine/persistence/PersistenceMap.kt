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
        val json = Json(JsonConfiguration.Stable)
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

    fun getDouble(name: String, default: Double = 0.0): Double {
        return map[name] as? Double ?: default
    }

    fun getFloat(name: String, default: Float = 0f): Float {
        return map[name] as? Float ?: default
    }

    fun getBoolean(name: String, default: Boolean = false): Boolean {
        return map[name] as? Boolean ?: default
    }

    fun getInt(name: String, default: Int = 0): Int {
        return map[name] as? Int ?: default
    }

    fun getLong(name: String, default: Long = 0): Long {
        return map[name] as? Long ?: default
    }

    fun getString(name: String, default: String = ""): String {
        return map[name] as? String ?: default
    }

    override fun toString() = map.toString()

}

