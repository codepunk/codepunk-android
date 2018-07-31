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

package com.codepunk.codepunk

import android.content.Context
import android.widget.Toast
import com.codepunk.codepunklib.util.plugin.PluginManager
import java.util.*

abstract class DayPlugin(val context: Context) {
    abstract fun showGreeting()
}

class BaseDayPlugin(_context: Context): DayPlugin(_context) {
    override fun showGreeting() {
        Toast.makeText(
                context,
                "At least it's not Monday!",
                Toast.LENGTH_LONG)
                .show()
    }
}

class MondayPlugin(_context: Context): DayPlugin(_context) {
    override fun showGreeting() {
        Toast.makeText(
                context,
                "Looks like someone has a case of the Mondays!",
                Toast.LENGTH_LONG)
                .show()
    }
}

class ThursdayPlugin(_context: Context): DayPlugin(_context) {
    override fun showGreeting() {
        Toast.makeText(
                context,
                "Hooray, it's Thursday!",
                Toast.LENGTH_LONG)
                .show()
    }
}

class DayPluginManager(
        private val context: Context,
        pluginListener: PluginListener<DayPlugin, Calendar>? = null) :
        PluginManager<DayPlugin, Calendar>(pluginListener) {

    override fun isPluginStale(state: Calendar): Boolean {
        return activeState?.get(Calendar.DAY_OF_WEEK) != state.get(Calendar.DAY_OF_WEEK)
    }

    override fun newPlugin(state: Calendar): DayPlugin {
        return when (state.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> MondayPlugin(context)
            Calendar.THURSDAY -> ThursdayPlugin(context)
            else -> BaseDayPlugin(context)
        }
    }
}