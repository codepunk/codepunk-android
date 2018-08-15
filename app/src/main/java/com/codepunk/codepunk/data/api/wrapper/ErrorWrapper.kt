package com.codepunk.codepunk.data.api.wrapper

class ErrorWrapper<T>(private val t: Throwable) : ResultWrapper<T> {
    override fun getOrThrow(): T {
        throw (t)
    }
}