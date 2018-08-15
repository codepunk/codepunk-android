package com.codepunk.codepunk.data.api.wrapper

interface ResultWrapper<T> {
    fun getOrThrow(): T
}