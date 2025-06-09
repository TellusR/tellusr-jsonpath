package com.tellusr.framework.jsonpath.function

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

interface JPFun {
    fun process(result: List<JsonElement>?): JsonPrimitive?
}