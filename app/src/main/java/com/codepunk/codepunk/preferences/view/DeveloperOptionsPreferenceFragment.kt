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

package com.codepunk.codepunk.preferences.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.preference.ListPreference
import android.support.v7.preference.PreferenceFragmentCompat
import com.codepunk.codepunk.BuildConfig
import com.codepunk.codepunk.R
import com.codepunk.codepunk.api.environment.ApiEnvironment
import com.codepunk.codepunk.preferences.viewmodel.DeveloperPreferencesViewModel
import com.codepunk.doofenschmirtz.util.populate

/**
 * A preference fragment that displays developer options preferences to the user. By default,
 * developer options are not available to the user until they unlock the developer options
 * preference and authenticate themselves as a developer.
 */
class DeveloperOptionsPreferenceFragment :
        PreferenceFragmentCompat() {

    // region Properties

    /**
     * The [ViewModel] that stores developer options-related data used by this fragment.
     */
    private val developerPreferencesViewModel by lazy {
        ViewModelProviders.of(this).get(DeveloperPreferencesViewModel::class.java)
    }

    /**
     * The API environment preference.
     */
    private val apiEnvironmentPreference by lazy {
        findPreference(BuildConfig.PREFS_KEY_API_ENVIRONMENT) as ListPreference
    }

    // endregion Properties

    // region Inherited methods

    /**
     * Initializes the preference screen.
     */
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_developer_options, rootKey)
        requireActivity().title = preferenceScreen.title

        apiEnvironmentPreference.populate(
                enumClass = ApiEnvironment::class.java,
                entry = { apiEnvironment -> requireContext().getString(apiEnvironment.nameResId) })

        with(developerPreferencesViewModel) {
            apiEnvironment.observe(this@DeveloperOptionsPreferenceFragment,
                    Observer { env ->
                        apiEnvironmentPreference.summary =
                                env?.let { getString(it.nameResId) } ?: ""
                    })
        }
    }

    // endregion Inherited methods
}
