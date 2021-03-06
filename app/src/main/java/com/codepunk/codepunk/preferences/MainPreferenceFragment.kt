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

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.widget.Toast
import com.codepunk.codepunk.BuildConfig
import com.codepunk.codepunk.R
import com.codepunk.codepunk.developer.DeveloperPasswordDialogFragment
import com.codepunk.codepunk.developer.DisableDeveloperOptionsDialogFragment
import com.codepunk.codepunk.preferences.DeveloperPreferencesViewModel.DeveloperOptionsState
import com.codepunk.codepunk.preferences.DeveloperPreferencesViewModel.DeveloperOptionsState.*
import com.codepunk.codepunk.util.*
import com.codepunk.doofenschmirtz.preference.TwoTargetSwitchPreference
import com.codepunk.doofenschmirtz.util.startLaunchActivity

// region Constants

/**
 * The request code used by the developer password dialog fragment.
 */
private const val DEVELOPER_PASSWORD_REQUEST_CODE = 0

/**
 * The request code used by the disable developer options dialog fragment.
 */
private const val DISABLE_DEVELOPER_OPTIONS_REQUEST_CODE = 1

/**
 * The total number of clicks required to unlock developer options.
 */
private const val DEV_OPTS_CLICKS_TO_UNLOCK: Int = 7

/**
 * The number of clicks remaining at which to show a [Toast] message.
 */
private const val DEV_OPTS_CLICKS_REMAINING_TOAST = 3

/**
 * The save state key for storing clicks remaining to unlock developer options.
 */
private const val SAVE_STATE_CLICKS_REMAINING = "clicksRemaining"

// endregion Constants

/**
 * A preference fragment that displays the main settings available to the user.
 */
class MainPreferenceFragment :
    PreferenceFragmentCompat(),
    Preference.OnPreferenceChangeListener,
    Preference.OnPreferenceClickListener {

    // region Fields

    /**
     * The number of clicks remaining to unlock developer options.
     */
    private var clicksToUnlockDeveloperOptions = 0

    /**
     * The [ViewModel] that stores main/general preference data used by this fragment.
     */
    private val mainPreferencesViewModel by lazy {
        ViewModelProviders.of(this).get(MainPreferencesViewModel::class.java)
    }

    /**
     * The [ViewModel] that stores developer options-related preference data used by this fragment.
     */
    private val developerPreferencesViewModel by lazy {
        ViewModelProviders.of(this).get(DeveloperPreferencesViewModel::class.java)
    }

    /**
     * The developer options preference.
     */
    private val developerOptionsPreference by lazy {
        findPreference(BuildConfig.PREFS_KEY_DEV_OPTS_ENABLED) as TwoTargetSwitchPreference
    }

    /**
     * The about preference.
     */
    private val aboutPreference by lazy {
        findPreference(BuildConfig.PREFS_KEY_ABOUT)
    }

    // endregion Fields

    // region Lifecycle methods

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clicksToUnlockDeveloperOptions = when {
            savedInstanceState != null ->
                savedInstanceState.getInt(SAVE_STATE_CLICKS_REMAINING, DEV_OPTS_CLICKS_TO_UNLOCK)
            developerPreferencesViewModel.developerOptionsUnlocked.value == true -> 0
            else -> DEV_OPTS_CLICKS_TO_UNLOCK
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SAVE_STATE_CLICKS_REMAINING, clicksToUnlockDeveloperOptions)
    }

    // endregion Lifecycle methods

    // region Inherited methods

    /**
     * Processes the results of dialogs launched by preferences in this fragment.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            DEVELOPER_PASSWORD_REQUEST_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        data?.run {
                            developerPreferencesViewModel.updateDeveloperOptions(
                                true,
                                getStringExtra(EXTRA_DEVELOPER_PASSWORD_HASH)
                            )
                        }
                    }
                }
            }
            DISABLE_DEVELOPER_OPTIONS_REQUEST_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        developerPreferencesViewModel.updateDeveloperOptions(true)
                        // TODO Set api environment to "Production"
                        requireContext().startLaunchActivity()
                    }
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    /**
     * Sets listeners and connects to the [ViewModel].
     */
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_main, rootKey)
        requireActivity().title = preferenceScreen.title
        aboutPreference.onPreferenceClickListener = this
        developerOptionsPreference.onPreferenceClickListener = this
        developerOptionsPreference.onPreferenceChangeListener = this

        with(mainPreferencesViewModel) {
            appVersion.observe(
                this@MainPreferenceFragment,
                Observer { version ->
                    aboutPreference.summary = getString(R.string.prefs_about_summary, version)
                })
        }

        with(developerPreferencesViewModel) {
            developerOptionsState.observe(
                this@MainPreferenceFragment,
                Observer { state ->
                    onDeveloperOptionsStateChange(state ?: DeveloperOptionsState.LOCKED)
                })

            onDeveloperOptionsStateChange(
                developerOptionsState.value ?: DeveloperOptionsState.LOCKED
            )
        }
    }

    // endregion Inherited methods

    // region Implemented methods

    /**
     * Delays turning on/off of developer options until the result of associated dialogs.
     */
    // Preference.OnPreferenceChangeListener
    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        return when (preference) {
            developerOptionsPreference -> {
                val enabled = newValue as Boolean
                if (enabled) {
                    showDeveloperPasswordDialogFragment()
                    false
                } else {
                    showDisableDeveloperOptionsDialogFragment()
                    false
                }
            }
            else -> {
                true
            }
        }
    }

    // Preference.OnPreferenceClickListener
    override fun onPreferenceClick(preference: Preference?): Boolean {
        return when (preference) {
            aboutPreference -> {
                when {
                    clicksToUnlockDeveloperOptions > 1 -> {
                        clicksToUnlockDeveloperOptions--
                        onStepsToUnlockDeveloperOptionsChange(clicksToUnlockDeveloperOptions)
                    }
                    clicksToUnlockDeveloperOptions == 1 -> {
                        clicksToUnlockDeveloperOptions = 0
                        developerPreferencesViewModel.updateDeveloperOptions(true)
                    }
                    else -> onRedundantUnlockRequest()
                }
                false
            }
            developerOptionsPreference -> {
                when (developerPreferencesViewModel.developerOptionsState.value) {
                    DeveloperOptionsState.ENABLED ->
                        startActivity(Intent(ACTION_PREFERENCES).apply {
                            addCategory(CATEGORY_DEVELOPER)
                        })
                    DeveloperOptionsState.UNLOCKED ->
                        showDeveloperPasswordDialogFragment()
                    else -> { /* No action */
                    }
                }
                true
            }
            else -> {
                false
            }
        }
    }

    // endregion Implemented methods

    // region Private methods

    /**
     * Updates the preference screen based on the state of developer options (i.e.
     * [LOCKED], [UNLOCKED], or [ENABLED].)
     */
    private fun onDeveloperOptionsStateChange(state: DeveloperOptionsState) {
        developerOptionsPreference.isChecked = (state == DeveloperOptionsState.ENABLED)
        when (state) {
            DeveloperOptionsState.LOCKED ->
                preferenceScreen.removePreference(developerOptionsPreference)
            else -> preferenceScreen.addPreference(developerOptionsPreference)
        }
    }

    /**
     * Handles when the about preference is clicked when developer options have already been
     * unlocked.
     */
    private fun onRedundantUnlockRequest() {
        Toast.makeText(
            context,
            R.string.prefs_dev_opts_redundant_show_request,
            Toast.LENGTH_SHORT
        )
            .show()
    }

    /**
     * Handles when the number of clicks remaining to unlock developer options changes.
     */
    private fun onStepsToUnlockDeveloperOptionsChange(steps: Int) {
        if (steps in 1..DEV_OPTS_CLICKS_REMAINING_TOAST) {
            Toast.makeText(
                context,
                getString(R.string.prefs_dev_opts_steps_from_unlocking, steps),
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    /**
     * Shows the developer password dialog.
     */
    private fun showDeveloperPasswordDialogFragment() {
        with(requireFragmentManager()) {
            if (findFragmentByTag(DEVELOPER_PASSWORD_DIALOG_FRAGMENT_TAG) != null) {
                return
            }

            DeveloperPasswordDialogFragment.newInstance()
                .apply {
                    setTargetFragment(
                        this@MainPreferenceFragment,
                        DEVELOPER_PASSWORD_REQUEST_CODE
                    )
                }
                .show(this, DEVELOPER_PASSWORD_DIALOG_FRAGMENT_TAG)
        }
    }

    /**
     * Shows an OK/Cancel dialog confirming that the user wishes to disable developer options.
     */
    private fun showDisableDeveloperOptionsDialogFragment() {
        with(requireFragmentManager()) {
            if (findFragmentByTag(DISABLE_DEVELOPER_OPTIONS_DIALOG_FRAGMENT_TAG) != null) {
                return
            }

            DisableDeveloperOptionsDialogFragment.newInstance()
                .apply {
                    setTargetFragment(
                        this@MainPreferenceFragment,
                        DISABLE_DEVELOPER_OPTIONS_REQUEST_CODE
                    )
                }
                .show(this, DISABLE_DEVELOPER_OPTIONS_DIALOG_FRAGMENT_TAG)
        }
    }

    // endregion Private methods

    // region Companion object

    companion object {

        // region Properties

        /**
         * The fragment tag to use for the developer password dialog fragment.
         */
        private val DEVELOPER_PASSWORD_DIALOG_FRAGMENT_TAG =
            MainPreferenceFragment::class.java.name + ".DEVELOPER_PASSWORD_DIALOG"

        /**
         * The fragment tag to use for the disable developer options dialog fragment.
         */
        private val DISABLE_DEVELOPER_OPTIONS_DIALOG_FRAGMENT_TAG =
            MainPreferenceFragment::class.java.name + ".DISABLE_DEVELOPER_OPTIONS_DIALOG"

        // endregion Properties

    }

    // endregion Companion object
}