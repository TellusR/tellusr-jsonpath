package com.tellusr.framework.jsonpath.path

import kotlinx.serialization.json.JsonElement

abstract class JPBase {
    var child: JPBase? = null

    abstract fun get(jsonElement: JsonElement): List<JsonElement>?

    override fun toString(): String =
        "${this.javaClass.simpleName} ${child.toString()}"
}
