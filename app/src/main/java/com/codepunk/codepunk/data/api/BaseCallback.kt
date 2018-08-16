package com.codepunk.codepunk.data.api

import android.util.Log
import com.codepunk.codepunk.CodepunkApp.Companion.loginator
import com.codepunk.codepunk.data.api.throwable.NullResponseException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

abstract class BaseCallback<T, E>(
    val createError: (String?) -> E? = { null }
) : Callback<T> {
    override fun onResponse(call: Call<T>, response: Response<T>?) {
        // TODO Could also handle this with an OkHttpClient Response interceptor
        response?.apply {
            when {
                isSuccessful ->
                    onSuccess(call, response, body())
                else -> {
                    onFailure(call, response, createError(errorBody()?.string()))
                }
            }
        } ?: run {
            if (loginator.isLoggable(Log.WARN)) {
                loginator.w("The response was null")
            }
            onFailure(call, NullResponseException())
        }
    }

    open fun onSuccess(call: Call<T>, response: Response<T>?, body: T? = null) {
        // nop
    }

    open fun onFailure(call: Call<T>, response: Response<T>?, error: E? = null) {
        // nop
    }
}