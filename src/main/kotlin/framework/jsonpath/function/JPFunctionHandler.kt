package com.tellusr.framework.jsonpath.function

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

/**
 * Handles custom functions in JSON path expressions.
 * To add new function handlers, implement this interface and register using JPFunction.registerFunctionHandler.
 *
 * Example:
 * ```
 * class MyFunction : JPFunctionHandler {
 *     override val functionName = "myFunction"
 *     override fun process(param: String?, result: List<JsonElement>?): JsonPrimitive? {
 *         // Implementation
 *     }
 * }
 * JPFunction.registerFunctionHandler(MyFunction())
 * ```
 */
interface JPFunctionHandler {
    /**
     * Name of the function that this handler processes.
     * This name will be used to identify and invoke the function in JSON path expressions.
     */
    val functionName: String

    /**
     * Processes the function call with given parameters and input.
     *
     * @param param Optional parameter passed to the function
     * @param result Optional list of JSON elements that the function should process
     * @return Processed result as JsonPrimitive, or null if processing fails
     */
    fun process(param: String?, result: JsonElement?): JsonPrimitive?
}