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

import com.codepunk.codepunk.data.model.AuthToken
import com.codepunk.codepunk.data.model.GrantType
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded

interface AuthApi {

    @FormUrlEncoded
    @POST("oauth/token")
    fun authToken(
        @Field("grant_type") grantType: GrantType,
        @Field("username") userName: String,
        @Field("password") password: String,
        @Field("client_id") clientId: Int,
        @Field("client_secret") clientSecret: String,
        @Field("scope") scope: String = "*"
    ): Call<AuthToken>

}