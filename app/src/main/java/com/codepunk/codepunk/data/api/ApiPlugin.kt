/*
 * Copyright (C) 2018 Codepunk, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codepunk.codepunk.data.api

import com.codepunk.codepunk.util.EnumFieldConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

// region Constants

const val AUTHORIZATION = "Authorization"
const val BEARER = "Bearer"

// endregion Constants

/**
 * The base API environment plugin class.
 */
abstract class ApiPlugin : Interceptor {

    // region Properties

    /**
     * The [ApiEnvironment] value associated with this [ApiPlugin].
     */
    abstract val apiEnvironment: ApiEnvironment

    /**
     * The base URL to use for API calls.
     */
    abstract val baseUrl: String

    private var moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    /**
     * The [Retrofit] instance to use to make API calls in this [ApiEnvironment].
     */
    val retrofit: Retrofit by lazy {
        val client = OkHttpClient.Builder().also { builder ->
            onPrepareOkHttpClientBuilder(builder)
        }.build()

        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(EnumFieldConverterFactory())
            .addConverterFactory(
                MoshiConverterFactory.create(moshi)
            )
            .client(client)
            .build()
    }

    /**
     * The [AuthWebservice] instance for making auth API calls.
     */
    val authWebservice: AuthWebservice by lazy {
        retrofit.create(AuthWebservice::class.java)
    }

    /**
     * The [UserWebservice] instance for making user API calls.
     */
    val userWebservice: UserWebservice by lazy {
        retrofit.create(UserWebservice::class.java)
    }

    /* TODO TEMP */
    var accessToken = ""

    // endregion Properties

    // region Implemented methods

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return chain.proceed(
            when (request.header(NO_AUTHORIZATION)) {

                null -> request.newBuilder()
                    .addHeader(AUTHORIZATION, "$BEARER $accessToken")
                    .build()
                else -> request
            }
        )
    }

    // endregion Implemented methods

    // region Methods

    protected open fun onPrepareOkHttpClientBuilder(builder: OkHttpClient.Builder) {
        builder.addInterceptor(this)
    }

    // endregion Methods
}
