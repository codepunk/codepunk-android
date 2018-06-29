package com.codepunk.codepunk

import android.content.Context
import android.widget.Toast
import com.codepunk.codepunklib.util.plugin.PluginManager
import java.util.*

interface DayPlugin {
    fun showGreeting(context: Context)
}

class BaseDayPlugin: DayPlugin {
    override fun showGreeting(context: Context) {
        Toast.makeText(
                context,
                "At least it's not Monday!",
                Toast.LENGTH_LONG)
                .show()
    }
}

class MondayPlugin: DayPlugin {
    override fun showGreeting(context: Context) {
        Toast.makeText(
                context,
                "Looks like someone has a case of the Mondays!",
                Toast.LENGTH_LONG)
                .show()
    }
}

class DayPluginManager: PluginManager<DayPlugin, Calendar>() {
    override fun isPluginStale(state: Calendar?): Boolean {
        return isMonday(activeState) != isMonday(state)
    }

    override fun newPlugin(state: Calendar?): DayPlugin {
        return if (isMonday(state)) MondayPlugin() else BaseDayPlugin()
    }

    private fun isMonday(calendar: Calendar?): Boolean {
        return calendar?.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY
    }
}