package com.mechanica.engine.util

import kotlinx.serialization.*
import kotlinx.serialization.json.*


fun Map<*, *>.toJson(): JsonObject {
    return json {
        for (entry in this@toJson) {
            addSupportedType(entry.key.toString(), entry.value)
        }
    }
}

fun JsonObject.toStringValueMap(): Map<String, Any> {
    return this.mapNotNull {
        val value = it.value.getSupportedType()

        if (value != null) {
            it.key to value
        } else null

    }.toHashMap()
}

fun List<Pair<String, Any>>.toHashMap(): HashMap<String, Any> {
    val map = HashMap<String, Any>()
    for (p in this) {
        map[p.first] = p.second
    }
    return map
}

fun <V> JsonObjectBuilder.addSupportedType(key: String, value: V) {
    when (value) {
        is Number -> key to value
        is String -> key to value
        is Boolean -> key to value
        is JsonElement -> key to value
        is Map<*, *> -> key to value.toJson()
    }
}

fun JsonElement.getSupportedType(): Any? {
    return try {
        booleanOrNull ?: intOrNull ?: longOrNull ?: floatOrNull ?: doubleOrNull ?: contentOrNull
    } catch (ex: JsonException) {
        if (this is JsonObject) {
            return this.toStringValueMap()
        } else null
    }
}

fun Decoder.toJsonObject(): JsonObject {
    val input = this as? JsonInput ?: throw SerializationException("Expected Json Input")
    return input.decodeJson() as? JsonObject ?: throw SerializationException("Expected JsonArray")
}


class StringMapSerializer() : DeserializationStrategy<Map<String, Any>>, SerializationStrategy<Map<String, Any>> {

    override val descriptor = SerialDescriptor("PersistenceMap")

    override fun deserialize(decoder: Decoder) = decoder.toJsonObject().toStringValueMap()

    override fun serialize(encoder: Encoder, value: Map<String, Any>) {
        JsonElementSerializer.serialize(encoder, value.toJson())
    }

    override fun patch(decoder: Decoder, old: Map<String, Any>): Map<String, Any> =
            throw UpdateNotSupportedException("Update not supported")

}