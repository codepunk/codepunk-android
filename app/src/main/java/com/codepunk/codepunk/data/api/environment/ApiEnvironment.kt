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

import android.support.annotation.StringRes
import com.codepunk.codepunk.R

/**
 * Indicates the API environment the app is currently using to connect to the back end. Each is
 * initialized with a [nameResId] that specifies the user-readable name of the environment, and
 * a [baseUrl] that will be used to construct API calls.
 */
enum class ApiEnvironment(@StringRes val nameResId: Int, val baseUrl: String) {
    /**
     * The production API environment.
     */
    PROD(
            R.string.api_env_production,
            "" /* TODO */),

    /**
     * The development API environment.
     */
    DEV(
            R.string.api_env_development,
            "" /* TODO */),

    /**
     * The local API environment, indicating that we are connected to a local/virtual box.
     */
    LOCAL(
            R.string.api_env_local,
            "https://codepunk.test")
}
