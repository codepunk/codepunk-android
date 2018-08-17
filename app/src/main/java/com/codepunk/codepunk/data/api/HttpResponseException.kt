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

import org.json.JSONObject
import retrofit2.Response

private const val MESSAGE = "message"

/**
 * An [Exception] that indicates that an error was encountered in an otherwise successful
 * HTTP response.
 */
class HttpResponseException(message: String, private val code: Int) : RuntimeException(message) {

    /**
     * The http response code.
     */
    val responseCode: Int
        get() = code

    /**
     * Secondary constructor that takes a [Response] object. Note that this will still create
     * an [HttpResponseException] with a null `message`.
     */
    constructor(response: Response<*>) : this(
        JSONObject(response.errorBody()?.string()).optString(MESSAGE),
        response.code()
    )
}