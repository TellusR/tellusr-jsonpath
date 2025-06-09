package com.tellusr.framework.jsonpath

import kotlinx.serialization.json.JsonElement

abstract class JPBase {
    var child: JPBase? = null

    abstract fun get(parent: JsonElement): List<JsonElement>?

    override fun toString(): String =
        "${this.javaClass.simpleName} ${child.toString()}"
}
