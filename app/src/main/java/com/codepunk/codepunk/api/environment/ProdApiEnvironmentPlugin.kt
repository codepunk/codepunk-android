package com.codepunk.codepunk.api.environment

class ProdApiEnvironmentPlugin: ApiEnvironmentPlugin() {
    override val apiEnvironment: ApiEnvironment
        get() = ApiEnvironment.PROD
}