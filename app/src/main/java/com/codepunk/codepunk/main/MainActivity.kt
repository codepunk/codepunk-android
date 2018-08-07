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

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.codepunk.codepunk.CodepunkApp.Companion.loginator
import com.codepunk.codepunk.R
import com.codepunk.codepunk.util.ACTION_PREFERENCES
import java.util.*

/**
 * The main [Activity] for the Codepunk app.
 */
class MainActivity : AppCompatActivity() {

    // region Properties

    /**
     * An instance of [DayPluginator] that displays helpful toasts depending on the day of the
     * week.
     */
    private val dayPluginManager = DayPluginator(this)

    // endregion Properties

    // region Lifecycle methods

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dayPluginManager.get(Calendar.getInstance()).showGreeting()

        // TODO TEMP Always open settings
        startActivity(Intent(ACTION_PREFERENCES))
        // END TEMP
    }

    override fun onResume() {
        super.onResume()

        if (loginator.isLoggable(Log.DEBUG)) {
            loginator.d("This is a debug log entry")
        }
        if (loginator.isLoggable(Log.ERROR)) {
            loginator.e("This is an error log entry", Throwable("Don't do that"))
        }
        if (loginator.isLoggable(Log.INFO)) {
            loginator.i("This is an info log entry")
        }
        if (loginator.isLoggable(Log.VERBOSE)) {
            loginator.v("This is a verbose log entry")
        }
        if (loginator.isLoggable(Log.WARN)) {
            loginator.w("This is a warning log entry")
        }
    }

    // endregion Lifecycle methods

    // region Inherited methods

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * Handles the various menu options.
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.menu_settings -> {
                startActivity(Intent(ACTION_PREFERENCES))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // endregion Inherited methods
}
