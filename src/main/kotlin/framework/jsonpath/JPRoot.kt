package com.tellusr.framework.jsonpath

import kotlinx.serialization.json.JsonElement

class JPRoot(val key: String = "$"): JPBase() {
    override fun get(je: JsonElement): List<JsonElement>? =
        if(child == null) {
            listOf(je)
        }
        else {
            child!!.get(je)
        }
}
