package com.codepunk.codepunk.api.environment

class LocalApiEnvironmentPlugin: ApiEnvironmentPlugin() {
    override val apiEnvironment: ApiEnvironment
        get() = ApiEnvironment.LOCAL
}