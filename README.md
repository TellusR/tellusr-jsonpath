# JsonPath for Kotlin

A lightweight JSONPath implementation for Kotlin that enables extracting data from JSON structures using path
expressions.

## Features

Supports a subset of JSONPath syntax with the following features:

- Root access (`$`)
- Property access using dot notation (`$.store.book`) or bracket notation (`$['store']['book']`)
- Array access using index (`[0]`) or wildcard (`[*]`)
- Array slicing (`[start:end]`)
- Functions: `count()`, `json()`, `join()`, `csv()`, `format()`

## Installation

Add the dependency to your build.gradle.kts:

Add the dependency to your `build.gradle.kts`:

```kotlin
repositories {
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/tellusr/framework")
        credentials {
            username = project.findProperty("gpr.user")?.toString() 
                ?: System.getenv("GITHUB_USERNAME")
                ?: System.getenv("GH_USERNAME")
            password = project.findProperty("gpr.key")?.toString() 
                ?: System.getenv("GITHUB_TOKEN") 
                ?: System.getenv("GH_TOKEN")
        }
    }
}

dependencies {
    implementation("com.tellusr:tellusr-jsonpath:0.9.1")
}
```


## Usage

Extract data from JSON using JsonPath expressions:

```kotlin
fun jsonPathExample() {
    val testJson = """
            {
                "docScore": 0.5,
                "title": "Test Document"
            }
        """.trimIndent()
    val jsonElement = Json.parseToJsonElement(testJson)


    // Returns a JsonElements
    val result = JsonPath("$['docScore']").eval(jsonElement)

    // Does eval and converts to a string
    val result = JsonPath("$['docScore'].json()").evalContent(jsonElement)
}
```

## Adding Function Handler

Our JSONPath library supports custom functions through the interface. This allows you 
to extend the functionality with your own specialized operations that can be used 
directly in JSONPath expressions. `JPFunctionHandler`

1. To add a new function, follow these steps:

```kotlin
import com.tellusr.framework.jsonpath.function.JPFunctionHandler
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

class MyFunction : JPFunctionHandler {
// Define the function name to be used in JSONPath expressions
override val functionName = "myFunction"

    // Implement the logic for your function
    override fun process(param: String?, result: List<JsonElement>?): JsonPrimitive? {
        // param: The optional parameter from the function call
        // result: The result from evaluating the JSONPath expression before the function
        
        // Example implementation:
        return when {
            result.isNullOrEmpty() -> JsonPrimitive("No results")
            param.isNullOrEmpty() -> JsonPrimitive("Parameter missing")
            else -> JsonPrimitive("$param was applied to ${result.size} elements")
        }
    }
}
```

# JsonPath for Kotlin Overview
This library provides a lightweight JSONPath implementation for Kotlin, allowing you to extract data from JSON structures using path expressions.
## Core Features
- Access JSON elements using path expressions
- Support for dot notation () or bracket notation () `$.store.book``$['store']['book']`
- Array access with indexes or wildcards
- Built-in functions like , , , , and `count()``json()``join()``csv()``format()`

## Installation
To add the library to your project:
``` kotlin
repositories {
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/tellusr/framework")
        credentials {
            username = project.findProperty("gpr.user")?.toString() 
                ?: System.getenv("GITHUB_USERNAME")
                ?: System.getenv("GH_USERNAME")
            password = project.findProperty("gpr.key")?.toString() 
                ?: System.getenv("GITHUB_TOKEN") 
                ?: System.getenv("GH_TOKEN")
        }
    }
}

dependencies {
    implementation("com.tellusr:tellusr-jsonpath:0.9.1")
}
```

## Basic Usage

``` kotlin
// Parse JSON
val testJson = """
    {
        "docScore": 0.5,
        "title": "Test Document"
    }
""".trimIndent()
val jsonElement = Json.parseToJsonElement(testJson)

// Get raw JsonElements
val result = JsonPath("$['docScore']").eval(jsonElement)

// Get string result with function transformations
val stringResult = JsonPath("$['docScore'].json()").evalContent(jsonElement)
```

## Creating Custom Functions

You can extend functionality by implementing the interface: `JPFunctionHandler`

``` kotlin
import com.tellusr.framework.jsonpath.function.JPFunctionHandler
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

class MyFunction : JPFunctionHandler {
    override val functionName = "myFunction"
    
    override fun process(param: String?, result: List<JsonElement>?): JsonPrimitive? {
        // Implementation logic here
        return JsonPrimitive("Custom function result")
    }
}
```
