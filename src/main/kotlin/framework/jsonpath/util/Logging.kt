package com.tellusr.framework.jsonpath.util

import org.slf4j.LoggerFactory


object AppInfo {
    val version: String = AppInfo::class.java.getPackage().implementationVersion ?: "(Unversioned)"
    val group: String = "com.tellusr"
}


fun Any.getAutoNamedLogger() = if (javaClass.enclosingClass != null && javaClass.simpleName.contains("Companion")) {
    javaClass.declaringClass
} else {
    javaClass
}.let {
    LoggerFactory.getLogger(it)
}


val Throwable.stackTraceTopString: String?
    get() {
        var ex: Throwable? = this
        var res: String? = null
        while (res == null && ex != null) {
            res = ex.stackTrace.firstOrNull {
                it.className.startsWith(AppInfo.group)
            }?.toString()
            ex = ex.cause
        }
        return res
    }


val Throwable.messageAndCrumb: String
    get() {
        return "${javaClass.simpleName}: ${localizedMessage} (${stackTraceTopString ?: "-"})"
    }
