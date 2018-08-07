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

package com.codepunk.codepunk.main

import android.content.Context
import android.widget.Toast
import com.codepunk.doofenschmirtz.util.pluginator.Pluginator
import java.util.*

/**
 * The DayPlugin class, used to demonstrate [Pluginator] functionality.
 */
abstract class DayPlugin(val context: Context) {
    abstract fun showGreeting()
}

/**
 * A basic version of the DayPlugin class, which displays an "At least it's not Monday!" Toast.
 */
class BaseDayPlugin(_context: Context): DayPlugin(_context) {
    override fun showGreeting() {
        Toast.makeText(
                context,
                "At least it's not Monday!",
                Toast.LENGTH_LONG)
                .show()
    }
}

/**
 * A Monday-specific version of the DayPlugin class, which displays a "Looks like someone has a
 * case of the Mondays!" Toast.
 */
class MondayPlugin(_context: Context): DayPlugin(_context) {
    override fun showGreeting() {
        Toast.makeText(
                context,
                "Looks like someone has a case of the Mondays!",
                Toast.LENGTH_LONG)
                .show()
    }
}

/**
 * A Thursday-specific version of the DayPlugin class, which displays a "Hooray, it's almost the
 * weekend!" Toast.
 */
class ThursdayPlugin(_context: Context): DayPlugin(_context) {
    override fun showGreeting() {
        Toast.makeText(
                context,
                "Hooray, it's almost the weekend!",
                Toast.LENGTH_LONG)
                .show()
    }
}

/**
 * A [Pluginator] that manages [DayPlugin]s based on the day of the week.
 */
class DayPluginator(
        private val context: Context,
        pluginListener: PluginListener<DayPlugin, Calendar>? = null) :
        Pluginator<DayPlugin, Calendar>(pluginListener) {

    /**
     * Marks the active plugin as stale depending on the value returned by [getValue].
     */
    override fun isPluginStale(state: Calendar): Boolean {
        return activeState == null || getValue(activeState) != getValue(state)
    }

    /**
     * Creates a new [DayPlugin] instance depending on the day of the week.
     */
    override fun newPlugin(state: Calendar): DayPlugin {
        return when (state.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> MondayPlugin(context)
            Calendar.THURSDAY -> ThursdayPlugin(context)
            else -> BaseDayPlugin(context)
        }
    }

    /**
     * Returns an integer corresponding to the day of the week if the day is "special" (i.e.
     * Monday or Thursday); otherwise returns -1.
     */
    private fun getValue(calendar: Calendar?): Int {
        return when (calendar?.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> Calendar.MONDAY
            Calendar.THURSDAY -> Calendar.THURSDAY
            else -> -1
        }
    }
}