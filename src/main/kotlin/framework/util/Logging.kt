package com.tellusr.framework.util

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.PrintWriter
import java.io.StringWriter
import java.time.Instant
import kotlin.reflect.jvm.jvmName


fun Any.getLogableName() = if (this::class.isCompanion) {
    this::class.java.declaringClass.simpleName
} else {
    this::class.simpleName ?: this::class.jvmName
}

fun Any.getAutoNamedLogger() = getLogger(getLogableName())

/**
 * A very handy extension function to retrieve a logger. Having all classes use this
 * centralized declaration also has the advantage that we are certain all instances using
 * this provider will follow the very same naming scheme.
 *
 * Also, we specify the return type as Logger (so not a nullable type). LoggerFactory.getLogger\
()
 * is a so-called «platform call», which makes it impossible for the kotlin compiler to be
 * certain about nullability, but we are willing to take the risk!
 */
fun getLogger(name: String): Logger = LoggerFactory.getLogger(name)

fun getLogger(): Logger = LoggerFactory.getLogger("-")

class LogEvery(private val logger: Logger, private val seconds: Long = 60) {
    var nextLogInstant = Instant.EPOCH

    val log: Logger?
        get() {
            if (nextLogInstant.isBefore(Instant.now())) {
                nextLogInstant = Instant.now().plusSeconds(seconds)
                return logger
            }
            return null
        }
}


val Throwable.stackTraceString: String
    get() {
        val stringWriter = StringWriter()
        this.printStackTrace(PrintWriter(stringWriter))
        return stringWriter.toString()
    }


val Throwable.tellusrStackTrace: String
    get() = listOf("${javaClass.simpleName}: ${localizedMessage ?: message}").plus(stackTrace.filter {
        it.className.startsWith("com.tellusr")
    }).joinToString("\n  ")


val Throwable.jsonTellusrStackTrace: JsonArray
    get() = listOf("${javaClass.simpleName}: ${localizedMessage ?: message}").plus(stackTrace.filter {
        it.className.startsWith("com.tellusr")
    }).map {
        JsonPrimitive(it.toString())
    }.let {
        JsonArray(it)
    }


val Throwable.stackTraceTopString: String?
    get() {
        var ex: Throwable? = this
        var res: String? = null
        while (res == null && ex != null) {
            res = ex.stackTrace.firstOrNull {
                it.className.startsWith("com.tellusr")
            }?.toString()
            ex = ex.cause
        }
        return res
    }


val Throwable.messageAndCrumb: String
    get() {
        return "${javaClass.simpleName}: ${localizedMessage} (${stackTraceTopString ?: "-"})"
    }


inline val currentTellusrStackTrace: String get() {
    try {
        throw Exception("stacktrace")
    } catch (t: Throwable) {
        return t.tellusrStackTrace
    }
}
