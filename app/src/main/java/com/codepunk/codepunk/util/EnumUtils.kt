package com.codepunk.codepunk.util

// TODO Move to codepunklib. Or delete??

inline fun <reified T : Enum<T>> enumContains(name: String): Boolean {
    return enumValues<T>().any { it.name == name}
}

inline fun <reified T : Enum<T>> enumValueOf(name: String, defaultValue: T): T {
    return try {
        enumValueOf(name)
    } catch (e: NoSuchElementException) {
        defaultValue
    }
}
