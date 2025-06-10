package com.tellusr.framework.jsonpath.path

import com.tellusr.framework.jsonpath.function.JPFunCsv
import com.tellusr.framework.jsonpath.function.JPFunFormat
import kotlinx.serialization.json.*


class JPFunction(val f: String) {
    var param: String = ""

    fun process(result: List<JsonElement>?): JsonPrimitive? {
        val v = when (f) {
            "count" -> JsonPrimitive(result?.size ?: 0)
            "json" -> result?.let {
                if(it.size == 1) {
                    JsonPrimitive(
                        "```\n" +
                                jsonEncoder.encodeToString(it.first())
                                + "\n```"
                    )
                }
                else {
                    JsonPrimitive(
                        "```\n" +
                                jsonEncoder.encodeToString(it)
                                + "\n```"
                    )

                }
            }

            "join" -> result?.joinToString {
                it.jsonPrimitive.content
            }?.let {
                JsonPrimitive(it)
            }

            "csv" -> JPFunCsv(param).process(result)
            "format" -> JPFunFormat(param).process(result)

            else -> JsonNull
        }
        return v
    }


    companion object {
        val jsonEncoder = Json {
            prettyPrint = true
        }
    }
}
