package com.codepunk.codepunk.api.environment

enum class ApiEnvironment(val baseUrl: String) {
    PROD(""),
    DEV(""),
    LOCAL("https://codepunk.test")


}