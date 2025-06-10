package com.tellusr.framework.jsonpath

import com.tellusr.framework.jsonpath.path.JPArray
import com.tellusr.framework.jsonpath.path.JPBase
import com.tellusr.framework.jsonpath.path.JPFunction
import com.tellusr.framework.jsonpath.path.JPObject
import com.tellusr.framework.jsonpath.path.JPRoot
import com.tellusr.framework.jsonpath.path.JPTokenizer
import com.tellusr.framework.jsonpath.util.getAutoNamedLogger
import com.tellusr.framework.jsonpath.util.messageAndCrumb
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*


/**
 * JsonPath evaluates path expressions to extract data from JSON structures.
 * Supports object property access, array indexing, and functions.
 *
 * Supported path features:
 * - Root access ($)
 * - Property access (dot notation or ['property'])
 * - Array access ([index] or [*] for all elements)
 * - Array slicing ([start:end])
 * - Functions: count(), json(), join(), csv(), format()
 *
 * Example usage:
 * ```kotlin
 * val json = Json.parseToJsonElement("""
 *   {
 *     "store": {
 *       "books": [
 *         { "title": "Book 1", "price": 10 },
 *         { "title": "Book 2", "price": 20 }
 *       ]
 *     }
 *   }
 * """)
 *
 * val path = JsonPath("$.store.books[*].title")
 * val titles = path.eval(json) // Returns ["Book 1", "Book 2"]
 * ```
 *
 * @property path The JSON path expression to evaluate
 */
class JsonPath(val path: String) {
    private val stack = mutableListOf<JPBase>()
    private var tailingFunction: JPFunction? = null


    init {
        compile(path)
    }


    /**
     * Adds a new path element to the evaluation stack.
     * Sets the element as child of the last element in stack.
     *
     * @param element The path element to add
     */
    private fun add(element: JPBase) {
        // Set the new element as child of the last element in stack
        last()?.child = element
        // Add the new element to the stack
        stack.add(element)
    }


    /**
     * Returns the last element in the evaluation stack.
     *
     * @return The last path element or null if stack is empty
     */
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

    /**
     * Gets the root key from the path expression.
     * Useful for applications that wants to use this
     * as a variable parser that links the root key
     * to a variable name or container key
     *
     * @return The root key or null if not present
     */
    fun rootKey(): String? = (stack.firstOrNull() as? JPRoot)?.key

    /**
     * Evaluates the path expression against a JSON element.
     *
     * @param root The JSON element to evaluate against
     * @return List of matched JSON elements or null if evaluation fails
     */
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

    /**
     * Evaluates the path and returns results as formatted string.
     * Converts matched elements to string representation.
     *
     * @param root The JSON element to evaluate against
     * @return String representation of matched elements or null if none found
     */
    fun evalContent(root: JsonElement): String? =
        // Evaluate the path and transform results to string representation
        eval(root)?.mapNotNull { result ->
            when (result) {
                // Skip null values
                is JsonNull -> null
                // Extract primitive values directly
                is JsonPrimitive -> result.jsonPrimitive.contentOrNull
                // Convert complex objects to JSON string
                else -> jsonEncoder.encodeToString<JsonElement>(result)
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
