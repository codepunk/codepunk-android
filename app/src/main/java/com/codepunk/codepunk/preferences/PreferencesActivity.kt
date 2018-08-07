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

package com.codepunk.codepunk.preferences

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceDialogFragmentCompat
import android.support.v7.preference.PreferenceFragmentCompat
import android.view.MenuItem
import com.codepunk.codepunk.preferences.view.DeveloperOptionsPreferenceFragment
import com.codepunk.codepunk.preferences.view.MainPreferenceFragment
import com.codepunk.codepunk.util.CATEGORY_DEVELOPER
import com.codepunk.doofenschmirtz.preference.displayCustomPreferenceDialogFragment

/**
 * The [Activity] that will serve as the container for all preference-related fragments.
 */
class PreferencesActivity : AppCompatActivity(),
        PreferenceFragmentCompat.OnPreferenceDisplayDialogCallback {

    // region Lifecycle methods

    /**
     * Creates the appropriate preference fragment based on the category supplied
     * in the [Intent].
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (supportFragmentManager.findFragmentById(android.R.id.content) == null) {
            supportFragmentManager
                    .beginTransaction()
                    .add(android.R.id.content, createFragment(intent))
                    .commit()
        }
    }

    // endregion Lifecycle methods

    // region Inherited methods

    /**
     * Changes the default "Up" behavior to always go "back" instead of "up".
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                // Always go back instead of "Up"
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // endregion Inherited methods

    // region Implemented methods

    /**
     * Provides a way to display custom [PreferenceDialogFragmentCompat]s.
     */
    // PreferenceFragmentCompat.OnPreferenceDisplayDialogCallback
    override fun onPreferenceDisplayDialog(
            caller: PreferenceFragmentCompat,
            pref: Preference?): Boolean {
        return caller.displayCustomPreferenceDialogFragment(pref)
    }

    // endregion Implemented methods

    // region Companion object

    companion object {

        // region Methods

        /**
         * Creates a [PreferenceFragmentCompat] associated with the category passed in the intent.
         */
        private fun createFragment(intent: Intent): PreferenceFragmentCompat {
            intent.categories?.run {
                for (category in this) {
                    when (category) {
                        CATEGORY_DEVELOPER -> return DeveloperOptionsPreferenceFragment()
                    }
                }
            }
            return MainPreferenceFragment()
        }

        // endregion Methods

    }

    // endregion Companion object
}
