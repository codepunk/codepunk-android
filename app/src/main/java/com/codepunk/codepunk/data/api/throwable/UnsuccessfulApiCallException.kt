package com.codepunk.codepunk.data.api.throwable

import okhttp3.ResponseBody

class UnsuccessfulApiCallException : RuntimeException {

    val responseBody: ResponseBody?

    constructor(responseBody: ResponseBody?) : super() {
        this.responseBody = responseBody
    }

    constructor(responseBody: ResponseBody?, message: String?) : super(message) {
        this.responseBody = responseBody
    }

    constructor(responseBody: ResponseBody?, message: String?, cause: Throwable?) : super(
        message,
        cause
    ) {
        this.responseBody = responseBody
    }

    constructor(responseBody: ResponseBody?, cause: Throwable?) : super(cause) {
        this.responseBody = responseBody
    }

}