package com.tellusr.framework.jsonpath.function

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

class JPFunFormat : JPFunctionHandler {
    override val functionName: String = "format"

    override fun process(param: String?, result: JsonElement?): JsonPrimitive? {
        val formatted = when(result) {
            is JsonArray -> {
                result.mapNotNull {
                    when (it) {
                        is JsonPrimitive -> it.jsonPrimitive.contentOrNull
                        else -> null
                    }
                }.joinToString("\n")
            }

            is JsonObject -> result.entries.joinToString("\n") {
                "${it.key}: ${it.value.jsonPrimitive.contentOrNull}"
            }

            is JsonPrimitive -> result.contentOrNull

            else -> null
        }?.ifBlank {
            null
        }?.let { value ->
            (param ?: "").replace("{}", value)
        } ?: ""

        return JsonPrimitive(formatted)
    }
}
