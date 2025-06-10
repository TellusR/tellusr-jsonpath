package com.tellusr.framework.jsonpath.path


/**
 * Tokenizer for JSON path expressions.
 * Processes input path string into individual tokens for JSON path navigation.
 *
 * @property path The JSON path expression string to tokenize
 */
class JPTokenizer(val path: String) {
    /**
     * Represents a single token in the JSON path expression.
     *
     * @property v The string value of the token
     */
    class JPToken(val v: String) {
        fun toJP(): JPBase =
            if (v == "$") {
                JPRoot()
            } else if (v.startsWith("'")) {
                JPObject(v.trim('\''))
            } else {
                JPArray(v)
            }
    }


    /** Starting position of current token */
    var start = 0

    /** Current position in the path string */
    var pos = 0

    /**
     * Returns the current token string.
     * @return Current token or empty string if token is empty
     */
    fun token(): String {
        return if(!isTokenEmpty())
            path.substring(start, pos)
        else
            ""
    }

    /**
     * Checks if current token is empty.
     * @return true if start position equals current position
     */
    fun isTokenEmpty(): Boolean = (start == pos)

    /**
     * Gets character at current position.
     * @return Character at current position in path string
     */
    fun char(): Char = path.get(pos)

    /**
     * Increments current position.
     */
    fun inc() {
        ++pos
    }

    /**
     * Moves start position to current position.
     */
    fun startToken() {
        start = pos
    }

    /**
     * Checks if there are more characters to process.
     * @return true if current position is less than path length
     */
    fun hasMore(): Boolean {
        return pos < path.length
    }
}
