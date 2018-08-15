package com.codepunk.codepunk.data.api.wrapper

class DataWrapper<T>(private val data: T) : ResultWrapper<T> {
    override fun getOrThrow(): T {
        return data
    }
}