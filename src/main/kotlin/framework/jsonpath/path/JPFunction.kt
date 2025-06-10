package com.tellusr.framework.jsonpath.path

import com.tellusr.framework.jsonpath.function.JPFunctionHandler
import com.tellusr.framework.jsonpath.function.JPFunCsv
import com.tellusr.framework.jsonpath.function.JPFunFormat
import kotlinx.serialization.encodeToString
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
                                jsonEncoder.encodeToString<JsonElement>(it.first())
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

            else -> functionHandlers[f]?.process(param, result)
        }
        return v
    }


    companion object {

        fun registerFunctionHandler(h: JPFunctionHandler) {
            functionHandlers.put(h.functionName, h)
        }


        private val functionHandlers: MutableMap<String, JPFunctionHandler> = listOf<JPFunctionHandler>(
            JPFunCsv(),
            JPFunFormat()
        ).associateBy {
            it.functionName
        }.toMutableMap()

        private val jsonEncoder = Json {
            prettyPrint = true
        }
    }
}
