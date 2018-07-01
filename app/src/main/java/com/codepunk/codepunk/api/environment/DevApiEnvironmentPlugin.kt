package com.codepunk.codepunk.api.environment

class DevApiEnvironmentPlugin: ApiEnvironmentPlugin() {
    override val apiEnvironment: ApiEnvironment
        get() = ApiEnvironment.DEV
}