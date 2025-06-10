package com.tellusr.framework.jsonpath.function

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

class JPFunFormat : JPFunctionHandler {
    override val functionName: String = "format"

    override fun process(param: String?, result: List<JsonElement>?): JsonPrimitive? {
        val r = result?.mapNotNull {
            when (it) {
                is JsonPrimitive -> it.jsonPrimitive.contentOrNull
                else -> null
            }
        }?.joinToString("\n")

        val formatted = r?.ifBlank { null }?.let { value ->
            (param ?: "").replace("{}", value)
        } ?: ""
        return JsonPrimitive(formatted)
    }
}
