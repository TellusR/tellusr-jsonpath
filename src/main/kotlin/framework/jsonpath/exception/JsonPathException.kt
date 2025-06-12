package com.tellusr.framework.jsonpath.exception

open class JsonPathException(message: String, cause: Throwable?): Exception(message, cause) {
    class IllegalState(message: String, cause: Throwable? = null): JsonPathException(message, cause)
}
