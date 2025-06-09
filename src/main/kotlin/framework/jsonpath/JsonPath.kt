package com.tellusr.framework.jsonpath

import com.tellusr.framework.util.getAutoNamedLogger
import com.tellusr.framework.util.messageAndCrumb
import kotlinx.serialization.json.*


class JsonPath(val path: String) {
    private val stack = mutableListOf<JPBase>()
    private var tailingFunction: JPFunction? = null


    init {
        compile(path)
    }


    private fun add(element: JPBase) {
        // Set the new element as child of the last element in stack
        last()?.child = element
        // Add the new element to the stack
        stack.add(element)
    }


    private fun last(): JPBase? =
        // Return the last element from stack or null if stack is empty
        stack.lastOrNull()


    private fun compile(p: String) {
        // Initialize tokenizer with trimmed input path
        var tokenizer = JPTokenizer(p.trim())

        // Handle root element if path starts with $
        if (tokenizer.char() == '$') {
            add(JPRoot())
            tokenizer.inc()
            tokenizer.startToken()
        }

        while (tokenizer.hasMore()) {
            when (tokenizer.char()) {
                // Handle object property access with dot notation or array access
                '.', '[' -> {
                    if (!tokenizer.isTokenEmpty()) {
                        val token = tokenizer.token()
                        // Add root element for start token, otherwise add as object property
                        // The token is the name of the object property
                        if (stack.isEmpty())
                            add(JPRoot(token))
                        else
                            add(JPObject(token))
                        tokenizer.inc()
                        tokenizer.startToken()
                    } else {
                        // Skip empty tokens
                        tokenizer.inc()
                        tokenizer.startToken()
                    }
                }

                // Handle array index or quoted property access closing bracket
                ']' -> {
                    val token = tokenizer.token().trim()
                    if (token.startsWith("'")) {
                        // Handle quoted property names
                        add(JPObject(token.trim('\'')))
                    } else {
                        // Handle array indices
                        add(JPArray(token))
                    }
                    tokenizer.inc()
                    tokenizer.startToken()
                }

                // Handle function start, store function name
                '(' -> {
                    tailingFunction = JPFunction(tokenizer.token())
                    tokenizer.inc()
                    tokenizer.startToken()
                }

                // Handle function end, store function parameter
                ')' -> {
                    tailingFunction?.param = tokenizer.token()
                    tokenizer.inc()
                    tokenizer.startToken()
                }

                else -> tokenizer.inc()
            }
        }

        // Handle any remaining token as object property
        if (!tokenizer.isTokenEmpty()) {
            add(JPObject(tokenizer.token()))
        }
    }

    // Useful for variable parsers
    fun rootKey(): String? = (stack.firstOrNull() as? JPRoot)?.key

    fun eval(root: JsonElement): List<JsonElement>? {
        return try {
            // Log the current state of the stack for debugging
            logger.trace("Stack: " + stack.firstOrNull().toString())
            // Get results by evaluating the path against root element
            val res = stack.firstOrNull()?.get(root)
            // Apply tailing function if present, otherwise return direct results
            tailingFunction?.process(stack.firstOrNull()?.get(root))?.let {
                listOf(it)
            } ?: res
        } catch (ex: Throwable) {
            // Log any errors and return null on failure
            logger.info(ex.messageAndCrumb)
            null
        }
    }

    fun getContent(root: JsonElement): String? =
        // Evaluate the path and transform results to string representation
        eval(root)?.mapNotNull { result ->
            when (result) {
                // Skip null values
                is JsonNull -> null
                // Extract primitive values directly
                is JsonPrimitive -> result.jsonPrimitive.contentOrNull
                // Convert complex objects to JSON string
                else -> JsonPath.jsonEncoder.encodeToString(result)
            }
            // Join results with newlines and return null if empty
        }?.joinToString("\n")?.ifBlank { null }

    companion object {
        val logger = getAutoNamedLogger()
        val jsonEncoder = Json {
            prettyPrint = true
        }
    }
}
