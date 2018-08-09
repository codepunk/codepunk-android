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

package com.codepunk.codepunk.data.model

/**
 * An enumeration of the various grant types used in OAuth2 calls.
 */
enum class GrantType(val value: String) {

    /**
     * Used when an application exchanges an authorization code for an access token.
     */
    AUTHORIZATION_CODE("authorization_code"),

    /**
     * Used when applications request an access token to access their own resources, not on behalf
     * of a user.
     */
    CLIENT_CREDENTIALS("client_credentials"),

    /**
     * Used when the application uses a refresh token to obtain a new access token.
     */
    REFRESH_TOKEN("refresh_token"),

    /**
     * Used when the application exchanges the user’s username and password for an access token.
     */
    PASSWORD("password")

}
