package com.tellusr.framework.jsonpath

import com.tellusr.framework.jsonpath.exception.JsonPathException
import com.tellusr.framework.jsonpath.path.JPArray
import com.tellusr.framework.jsonpath.path.JPBase
import com.tellusr.framework.jsonpath.path.JPFunction
import com.tellusr.framework.jsonpath.path.JPObject
import com.tellusr.framework.jsonpath.path.JPRoot
import com.tellusr.framework.jsonpath.path.JPTokenizer
import com.tellusr.framework.jsonpath.util.getAutoNamedLogger
import com.tellusr.framework.jsonpath.util.messageAndCrumb
import kotlinx.serialization.json.*
import kotlin.collections.mapNotNull


/**
 * JsonPath evaluates path expressions to extract data from JSON structures.
 * Uses a linked list of path elements connected via next references with head and tail pointers.
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
    private var head: JPBase? = null
    private var tail: JPBase? = null
    private var tailingFunction: JPFunction? = null

    private val bracketStack: MutableList<Char> = mutableListOf()

    data class Error(val message: String, val offset: Int)

    private var errors: MutableList<Error>? = null

    fun addError(error: Error) {
        if (errors == null)
            errors = mutableListOf()

        errors!!.add(error)
    }

    fun errors(): List<Error> = errors?.toList() ?: emptyList()


    init {
        compile(path)
    }


    /**
     * Adds a new path element to the path chain.
     * Links it to the previous element via next reference and updates tail.
     *
     * @param element The path element to add
     */
    private fun add(element: JPBase) {
        if (head == null)
            head = element
        // Set the new element as child of tail
        tail?.next = element
        // Make the new element the new tail
        tail = element
    }

    private fun addRootOrObject(token: String) {
        if (head == null) {
            add(JPRoot(token))
        } else {
            add(JPObject(token))
        }
    }


    private fun compile(p: String) {
        // Initialize tokenizer with trimmed input path
        val tokenizer = JPTokenizer(p.trim())

        while (tokenizer.hasMore()) {
            if (!tokenizer.token().firstOrNull().let { it == '"' || it == '\'' }) {
                // Validate opening and closing brackets, unless we are in a quoted string
                if (tokenizer.char() in brackets.values) {
                    bracketStack.add(tokenizer.char())
                }
                if (tokenizer.char() in brackets.keys) {
                    if (bracketStack.isEmpty() || bracketStack.last() != brackets[tokenizer.char()]) {
                        addError(Error("Unexpected opening bracket - ${tokenizer.char()}", tokenizer.pos))
                    } else {
                        bracketStack.removeLast()
                    }
                }
            }

            when (tokenizer.char()) {
                // Handle object property access with dot notation or array access
                '.', '[', ' ', '\n' -> {
                    if (!tokenizer.isTokenEmpty()) {
                        val token = tokenizer.token()
                        // Add root element for start token, otherwise add as object property
                        // The token is the name of the object property
                        addRootOrObject(token)
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
                        // Quoted object keys needed to support spaces in property names
                        if (!token.endsWith("'")) {
                            addError(
                                Error(
                                    "Missing closing quote (${tokenizer.char()}) for property name",
                                    tokenizer.pos
                                )
                            )
                        }

                        // Handle quoted property names
                        add(JPObject(token.trim('\'')))
                    } else if (token.startsWith("\"")) {
                        // Some path suppliers might prefer double quotes, so we allow them here as well
                        if (!token.endsWith("\"")) {
                            addError(
                                Error(
                                    "Missing closing quote (${tokenizer.char()}) for property name",
                                    tokenizer.pos
                                )
                            )
                        }
                        // Handle quoted property names
                        add(JPObject(token.trim('\"')))
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
            addRootOrObject(tokenizer.token())
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
    fun rootKey(): String = (head as? JPRoot)?.key
        ?: throw JsonPathException.IllegalState("No root key found in path expression: $path - ($head)")

    /**
     * Evaluates the path expression against a JSON element.
     *
     * @param root The JSON element to evaluate against
     * @return List of matched JSON elements or null if evaluation fails
     */
    fun eval(root: JsonElement): JsonElement? = try {
        // Get results by evaluating the path against root element
        val res = head?.get(root)
        // Apply tailing function if present, otherwise return direct results
        tailingFunction?.process(head?.get(root)) ?: res
    } catch (ex: Throwable) {
        // Log any errors and return null on failure
        logger.info(ex.messageAndCrumb)
        null
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
        when (val e = eval(root)) {
            is JsonArray -> e.mapNotNull { result ->
                when (result) {
                    // Skip null values
                    is JsonNull -> null
                    // Extract primitive values directly
                    is JsonPrimitive -> result.jsonPrimitive.contentOrNull
                    // Convert complex objects to JSON string
                    else -> jsonEncoder.encodeToString<JsonElement>(result)
                }
                // Join results with newlines and return null if empty
            }.joinToString("\n")?.ifBlank { null }

            is JsonObject -> jsonEncoder.encodeToString<JsonElement>(e)
            is JsonPrimitive -> e.jsonPrimitive.contentOrNull
            else -> null
        }


    companion object {
        val logger = getAutoNamedLogger()
        val jsonEncoder = Json {
            prettyPrint = true
        }
        private val brackets = mapOf(']' to '[', ')' to '(')
    }
}
