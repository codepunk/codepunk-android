package com.codepunk.codepunk.data.api.throwable

import com.codepunk.codepunk.data.model.LaravelError

class LaravelErrorException : RuntimeException {

    val error: LaravelError?

    constructor(error: LaravelError?) : super(error?.message) {
        this.error = error
    }

    constructor(error: LaravelError?, cause: Throwable?) : super(error?.message, cause) {
        this.error = error
    }

}
