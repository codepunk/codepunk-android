package com.codepunk.codepunk.data.model

import com.squareup.moshi.Json

/**
 * An enumeration of the various token types used in OAuth2 calls.
 */
enum class TokenType(val value: String) {

    /**
     * A token type representing a Bearer Token, that is, a single string which acts as the
     * authentication of the API request, sent in an HTTP “Authorization” header.
     */
    @Json(name = "Bearer")
    BEARER("Bearer"),

    /**
     * A token type representing a MAC (Message Authentication Code) access authentication scheme,
     * an HTTP authentication method using a message authentication code (MAC) algorithm to provide
     * cryptographic verification of portions of HTTP requests.
     */
    @Json(name = "MAC")
    MAC("MAC")
}