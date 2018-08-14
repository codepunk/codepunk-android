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

import com.codepunk.codepunk.util.generateSSLSocketFactory
import okhttp3.OkHttpClient

/**
 * The local API environment plugin.
 */
class LocalApiPlugin : ApiPlugin() {

    // region Properties

    override val apiEnvironment: ApiEnvironment
        get() = ApiEnvironment.LOCAL

    override val baseUrl: String
        get() = "https://codepunk.test"

    // endregion Properties

    // region Inherited methods

    override fun onPrepareOkHttpClientBuilder(builder: OkHttpClient.Builder) {
        super.onPrepareOkHttpClientBuilder(builder)
        builder.generateSSLSocketFactory()
    }

    // endregion Inherited methods
}
