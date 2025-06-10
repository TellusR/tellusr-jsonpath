package com.tellusr.framework.jsonpath.path

import com.tellusr.framework.jsonpath.util.getAutoNamedLogger
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

class JPObject(val key: String) : JPBase() {
    override fun get(jsonElement: JsonElement): List<JsonElement>? {
        return jsonElement.jsonObject.get(key)?.let { element ->
            child?.get(element) ?: listOf(element)
        }
    }

    override fun toString(): String =
        "${javaClass.simpleName}($key) ${child.toString()}"

    companion object {
        val logger = getAutoNamedLogger()
    }
}
