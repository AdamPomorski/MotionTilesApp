package com.example.demoecotiles.data

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

@Serializable
sealed class JsonDataValueWrapper {
    @Serializable
    @SerialName("single")
    data class Single(val value: JsonDataValue) : JsonDataValueWrapper()

    @Serializable
    @SerialName("list")
    data class ListWrapper(val values: List<JsonDataValue>) : JsonDataValueWrapper()
}

object JsonDataValueWrapperSerializer : JsonContentPolymorphicSerializer<JsonDataValueWrapper>(JsonDataValueWrapper::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out JsonDataValueWrapper> {
        return if (element is JsonArray) {
            JsonDataValueWrapper.ListWrapper.serializer()
        } else {
            JsonDataValueWrapper.Single.serializer()
        }
    }
}

@Serializable(with = JsonDataValueWrapperSerializer::class)
sealed class FlexibleJsonDataValue
