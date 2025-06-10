package com.tellusr.framework.jsonpath.path

import kotlinx.serialization.json.JsonElement

class JPRoot(val key: String = "$"): JPBase() {
    override fun get(jsonElement: JsonElement): List<JsonElement>? =
        if(child == null) {
            listOf(jsonElement)
        }
        else {
            child!!.get(jsonElement)
        }
}
