package com.tellusr.framework.jsonpath.util

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlin.collections.mapNotNull

object JsonToString {
    fun jsonToString(je: JsonElement): String? =
        when (je) {
            is JsonPrimitive -> je.contentOrNull ?: "Null"

            is JsonArray -> je.mapNotNull {
                jsonToString(it)
            }.joinToString("\n\n").ifBlank {
                "Empty Array"
            }

            is JsonObject -> je.entries.joinToString("\n\n") {
                "${it.key}: ${jsonToString(it.value)}"
            }.ifBlank {
                "Empty Object"
            }
        }

}
