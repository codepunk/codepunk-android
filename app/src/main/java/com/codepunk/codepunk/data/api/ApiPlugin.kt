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

import com.codepunk.codepunk.util.MoshiJsonEnumConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * The base API environment plugin class.
 */
abstract class ApiPlugin {

    // region Properties

    /**
     * The [ApiEnvironment] value associated with this [ApiPlugin].
     */
    abstract val apiEnvironment: ApiEnvironment

    /**
     * The base URL to use for API calls.
     */
    abstract val baseUrl: String

    /**
     * The [Retrofit] instance to use to make API calls in this [ApiEnvironment].
     */
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiJsonEnumConverterFactory())
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder()
                        .add(KotlinJsonAdapterFactory())
                        .build()
                )
            )
            .apply { onPrepareRetrofitBuilder(this) }
            .build()
    }

    // endregion Properties

    // region Methods

    protected open fun onPrepareRetrofitBuilder(builder: Retrofit.Builder) {
        // No action
    }

    // endregion Methods

    // region Companion object

    companion object {

        // region Methods

        /**
         * Creates a new [ApiPlugin] based on [apiEnvironment].
         */
        fun newInstance(apiEnvironment: ApiEnvironment): ApiPlugin {
            return when (apiEnvironment) {
                ApiEnvironment.PROD -> ProdApiPlugin()
                ApiEnvironment.DEV -> DevApiPlugin()
                ApiEnvironment.LOCAL -> LocalApiPlugin()
            }
        }

        // endregion Methods
    }

    // endregion Companion object
}
