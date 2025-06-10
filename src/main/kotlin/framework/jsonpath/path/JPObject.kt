package com.tellusr.framework.jsonpath.path

import com.tellusr.framework.jsonpath.util.getAutoNamedLogger
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

class JPObject(val key: String) : JPBase() {
    override fun get(je: JsonElement): List<JsonElement>? {
        return je.jsonObject.get(key)?.let { element ->
            if (child != null) {
                child?.get(element)
            } else {
                listOf(element)
            }
        }
    }

    override fun toString(): String =
        "${javaClass.simpleName}($key) ${child.toString()}"

    companion object {
        val logger = getAutoNamedLogger()
    }
}
