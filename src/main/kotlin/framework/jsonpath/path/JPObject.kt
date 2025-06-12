package com.tellusr.framework.jsonpath.path

import com.tellusr.framework.jsonpath.util.getAutoNamedLogger
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.jsonObject

class JPObject(val key: String) : JPBase() {
    override fun get(jsonElement: JsonElement): JsonElement? {
        return jsonElement.jsonObject.get(key)?.let { element ->
            next?.let { next ->
                next.get(element) ?: JsonNull
            } ?: element
        }
    }

    override fun toString(): String =
        "${javaClass.simpleName}($key) ${next.toString()}"

    companion object {
        val logger = getAutoNamedLogger()
    }
}
