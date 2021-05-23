package com.mechanica.engine.util

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*


fun Map<*, *>.toJson(): JsonObject {
    return buildJsonObject {
        for (entry in this@toJson) {
            addSupportedType(entry.key.toString(), entry.value)
        }
    }
}

fun JsonObject.toStringValueMap(): Map<String, Any> {
    return this.mapNotNull {
        when (val value = it.value) {
            is JsonPrimitive -> {
                it.key to value.getSupportedType()
            }
            is JsonObject -> {
                it.key to value.toStringValueMap()
            }
            else -> null
        }
    }.toHashMap()
}

fun List<Pair<String, Any?>>.toHashMap(): HashMap<String, Any> {
    val map = HashMap<String, Any>()
    for (p in this) {
        val second = p.second
        if (second != null) map[p.first] = second
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

fun JsonPrimitive.getSupportedType(): Any? = booleanOrNull ?: intOrNull ?: longOrNull ?: floatOrNull ?: doubleOrNull ?: contentOrNull


fun Decoder.toJsonObject(): JsonObject {
    throw SerializationException("Expected Json Input")
//    val input = this as? JsonInput ?: throw SerializationException("Expected Json Input")
//    return input.decodeJson() as? JsonObject ?: throw SerializationException("Expected JsonArray")
}


class StringMapSerializer : DeserializationStrategy<Map<String, Any>>, SerializationStrategy<Map<String, Any>> {

    override val descriptor = object : SerialDescriptor {
        @ExperimentalSerializationApi
        override val elementsCount: Int
            get() = TODO("Not yet implemented")

        @ExperimentalSerializationApi
        override val kind: SerialKind
            get() = TODO("Not yet implemented")

        @ExperimentalSerializationApi
        override val serialName: String
            get() = TODO("Not yet implemented")

        @ExperimentalSerializationApi
        override fun getElementAnnotations(index: Int): List<Annotation> {
            TODO("Not yet implemented")
        }

        @ExperimentalSerializationApi
        override fun getElementDescriptor(index: Int): SerialDescriptor {
            TODO("Not yet implemented")
        }

        @ExperimentalSerializationApi
        override fun getElementIndex(name: String): Int {
            TODO("Not yet implemented")
        }

        @ExperimentalSerializationApi
        override fun getElementName(index: Int): String {
            TODO("Not yet implemented")
        }

        @ExperimentalSerializationApi
        override fun isElementOptional(index: Int): Boolean {
            TODO("Not yet implemented")
        }

    }

    override fun deserialize(decoder: Decoder) = decoder.toJsonObject().toStringValueMap()

    override fun serialize(encoder: Encoder, value: Map<String, Any>) {
//        JsonElementSerializer.serialize(encoder, value.toJson())
    }

}