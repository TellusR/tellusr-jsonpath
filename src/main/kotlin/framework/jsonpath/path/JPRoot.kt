package com.tellusr.framework.jsonpath.path

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull

class JPRoot(val key: String = "$"): JPBase() {
    override fun get(jsonElement: JsonElement): JsonElement? =
        next?.let { next ->
            next.get(jsonElement) ?: JsonNull
        } ?: jsonElement
}
