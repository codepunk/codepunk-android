package com.codepunk.codepunk

import android.content.Context
import android.widget.Toast
import com.codepunk.codepunklib.util.plugin.PluginManager
import java.util.*

abstract class DayPlugin(_context: Context) {
    val context: Context = _context
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

class DayPluginManager(
        _context: Context,
        _pluginListener: PluginListener<DayPlugin, Calendar>? = null):
        PluginManager<DayPlugin, Calendar>(_pluginListener) {
    private val context: Context = _context

    override fun isPluginStale(state: Calendar): Boolean {
        return activeState == null || isMonday(activeState!!) != isMonday(state)
    }

    override fun newPlugin(state: Calendar): DayPlugin {
        return if (isMonday(state)) MondayPlugin(context) else BaseDayPlugin(context)
    }

    private fun isMonday(calendar: Calendar): Boolean {
        return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY
    }
}