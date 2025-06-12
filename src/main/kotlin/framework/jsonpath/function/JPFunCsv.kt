package com.tellusr.framework.jsonpath.function

import kotlinx.serialization.json.*

class JPFunCsv: JPFunctionHandler {
    override val functionName: String = "csv"

    override fun process(param: String?, result: JsonElement?): JsonPrimitive? = when(result) {
        is JsonArray -> result.let { a ->
            val header = a.firstOrNull()?.jsonObject?.keys ?: listOf()
            val columns = a.map { o ->
                o.jsonObject.values.map { it.jsonPrimitive.contentOrNull ?: "" }
            }

            val lines: MutableList<String> = mutableListOf()

            header.joinToString(" | ").let { lines.add("| $it |") }
            header.joinToString(" | ") { "-----" }.let { lines.add("| $it |") }
            columns.forEach { col ->
                col.joinToString(" | ").let { lines.add("| $it |") }
            }
            lines.joinToString("\n").ifBlank {
                null
            }.let {
                JsonPrimitive(it)
            }
        }

        else -> null
    }
}
