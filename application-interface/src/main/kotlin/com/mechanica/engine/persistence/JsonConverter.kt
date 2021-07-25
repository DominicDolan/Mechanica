package com.mechanica.engine.persistence

import kotlinx.serialization.json.*

object JsonConverter {
    fun mapToJson(map: Map<*, *>): JsonObject {
        return buildJsonObject {
            for (entry in map) {
                putAny(entry.key.toString(), entry.value)
            }
        }
    }

    fun mapFromJson(jsonObject: JsonObject): Map<String, Any> {
        val list = jsonObject.mapNotNull {
            when (val value = it.value) {
                is JsonPrimitive -> {
                    val typedValue = value.toType()
                    if (typedValue == null) null
                    else it.key to typedValue
                }
                is JsonObject -> {
                    it.key to mapFromJson(value)
                }
                else -> null
            }
        }

        return mapOf(*list.toTypedArray())
    }

    private inline fun <reified V> JsonObjectBuilder.putAny(key: String, value: V) {
        when (value) {
            is Number -> put(key, value)
            is String -> put(key, value)
            is Boolean -> put(key, value)
            is JsonElement -> put(key, value)
            is Map<*, *> -> put(key, mapToJson(value))
        }
    }

    private fun JsonPrimitive.toType(): Any? = booleanOrNull ?: intOrNull ?: longOrNull ?: floatOrNull ?: doubleOrNull ?: contentOrNull
}