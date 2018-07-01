package com.codepunk.codepunk.api.environment

abstract class ApiEnvironmentPlugin {

    abstract val apiEnvironment: ApiEnvironment

    companion object {
        fun create(apiEnvironment: ApiEnvironment): ApiEnvironmentPlugin {
            return when(apiEnvironment) {
                ApiEnvironment.DEV -> DevApiEnvironmentPlugin()
                ApiEnvironment.LOCAL -> LocalApiEnvironmentPlugin()
                else -> ProdApiEnvironmentPlugin()
            }
        }
    }
}