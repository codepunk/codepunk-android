package com.codepunk.codepunk.api.environment

import com.codepunk.codepunklib.util.plugin.PluginManager

object ApiEnvironmentPluginManager: PluginManager<ApiEnvironmentPlugin, ApiEnvironment>() {

    //region Inherited methods

    override fun isPluginStale(state: ApiEnvironment): Boolean {
        return activeState != state
    }

    override fun newPlugin(state: ApiEnvironment): ApiEnvironmentPlugin {
        return ApiEnvironmentPlugin.create(state)
    }

    //endregion Inherited methods
}