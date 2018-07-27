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
        val context: Context,
        val pluginListener: PluginListener<DayPlugin, Calendar>? = null):
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