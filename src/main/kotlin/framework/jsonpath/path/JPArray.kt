package com.tellusr.framework.jsonpath.path

import com.tellusr.framework.jsonpath.util.getAutoNamedLogger
import kotlinx.serialization.json.*


class JPArray(val key: String) : JPBase() {
    fun range(jsonArray: JsonArray): IntRange {
        val lastElement: Int = jsonArray.size - 1
        key.trim().let { k ->
            return if (k.equals("*")) {
                IntRange(0, lastElement)
            } else if (k.contains(":")) {
                val parts = k.split(":")
                val start = parts.first().toIntOrNull() ?: 0
                val end = parts.last().toIntOrNull() ?: lastElement
                IntRange(start, end)
            } else if(k.equals("mid") || k.equals("middle")) {
                val element = lastElement / 2
                IntRange(element , element)
            } else {
                val index = k.toIntOrNull() ?: 0
                IntRange(index, index)
            }
        }
    }

    override fun get(jsonElement: JsonElement): JsonElement? =
        jsonElement.jsonArray.let { array ->
            val r = range(array)
            r.mapNotNull { i ->
                val element = array.get(i)
                next?.get(element) ?: element
            }.let { it ->
                when (it.size) {
                    0 -> empty
                    1 -> it.first()
                    else -> JsonArray(it)
                }
            }
        }


    override fun toString(): String =
        "${javaClass.simpleName}[$key] ${next.toString()}"


    companion object {
        val logger = getAutoNamedLogger()
        val jsonEncoder = Json { prettyPrint = true}

        val empty = JsonArray(emptyList())
    }
}

