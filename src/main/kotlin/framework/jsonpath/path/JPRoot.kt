package com.tellusr.framework.jsonpath.path

import kotlinx.serialization.json.JsonElement

class JPRoot(val key: String = "$"): JPBase() {
    override fun get(jsonElement: JsonElement): List<JsonElement>? =
        child?.get(jsonElement) ?: listOf(jsonElement)
}
