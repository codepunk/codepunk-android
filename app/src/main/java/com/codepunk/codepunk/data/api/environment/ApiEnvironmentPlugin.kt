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

package com.codepunk.codepunk.data.api.environment

import com.codepunk.codepunk.data.model.GrantType
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.lang.reflect.Type

/**
 * The base API environment plugin class.
 */
abstract class ApiEnvironmentPlugin {

    // region Properties

    /**
     * The [ApiEnvironment] value associated with this [ApiEnvironmentPlugin].
     */
    abstract val apiEnvironment: ApiEnvironment


    /**
     * The [Retrofit] instance to use to make API calls in this [ApiEnvironment].
     */
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(apiEnvironment.baseUrl)
            .addConverterFactory(moshiConverterFactory)
            .addConverterFactory(customConverterFactory)
            .build()
    }

    // endregion Properties

    // region Companion object

    companion object {

        // region Properties

        /**
         * The Moshi [Converter.Factory] to use for Retrofit API calls.
         */
        private val moshiConverterFactory: Converter.Factory by lazy {
            MoshiConverterFactory.create(moshi)
        }

        /**
         * The custom [Converter.Factory] to use for Retrofit API calls.
         * TODO Is there any better way to consolidate these custom converters?
         */
        private val customConverterFactory = object : Converter.Factory() {
            override fun stringConverter(
                type: Type?,
                annotations: Array<out Annotation>?,
                retrofit: Retrofit?
            ): Converter<*, String>? {
                return when (type) {
                    GrantType::class.java -> Converter<GrantType, String> { value ->
                        value.value
                    }
                    else -> null
                }
            }
        }

        /**
         * The Moshi instance
         */
        private val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        // endregion Properties

        // region Methods

        /**
         * Creates a new [ApiEnvironmentPlugin] based on [apiEnvironment].
         */
        fun newInstance(apiEnvironment: ApiEnvironment): ApiEnvironmentPlugin {
            return when (apiEnvironment) {
                ApiEnvironment.PROD -> ProdApiEnvironmentPlugin()
                ApiEnvironment.DEV -> DevApiEnvironmentPlugin()
                ApiEnvironment.LOCAL -> LocalApiEnvironmentPlugin()
            }
        }

        // endregion Methods
    }

    // endregion Companion object
}
