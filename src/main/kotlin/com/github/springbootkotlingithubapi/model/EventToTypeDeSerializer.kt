package com.github.springbootkotlingithubapi.model

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer

class EventToTypeDeSerializer(vc: Class<*>?) : StdDeserializer<Type?>(vc) {

    override fun deserialize(p0: JsonParser?, p1: DeserializationContext?): Type? {
        val node = p0?.codec?.readTree<JsonNode>(p0)?.asText()
        return node?.let { Type.of(it) }
    }

}