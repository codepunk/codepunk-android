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

package com.codepunk.codepunk.api.environment

import com.codepunk.codepunklib.util.plugin.PluginManager

object ApiEnvironmentPluginManager: PluginManager<ApiEnvironmentPlugin, ApiEnvironment>() {

    //region Inherited methods

    override fun isPluginStale(state: ApiEnvironment): Boolean {
        return activeState != state
    }

    override fun newPlugin(state: ApiEnvironment): ApiEnvironmentPlugin {
        return ApiEnvironmentPlugin.newInstance(state)
    }

    //endregion Inherited methods
}
