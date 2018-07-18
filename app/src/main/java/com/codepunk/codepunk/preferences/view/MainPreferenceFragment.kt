package com.codepunk.codepunk.preferences.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.preference.Preference
import android.widget.Toast
import com.codepunk.codepunk.BuildConfig
import com.codepunk.codepunk.R
import com.codepunk.codepunk.preferences.PreferencesActivity.PreferencesType
import com.codepunk.codepunk.preferences.viewmodel.DeveloperPreferencesViewModel
import com.codepunk.codepunk.preferences.viewmodel.DeveloperPreferencesViewModel.DeveloperOptionsState
import com.codepunk.codepunk.util.ACTION_SETTINGS
import com.codepunk.codepunk.util.EXTRA_PREFERENCES_TYPE
import com.codepunk.codepunklibstaging.preference.old.ExtendedPreferenceFragmentCompat

const val STEPS_TO_UNLOCK_SHOW_TOAST = 3

class MainPreferenceFragment:
        ExtendedPreferenceFragmentCompat(),
        Preference.OnPreferenceClickListener {

    //region Nested classes

    companion object {
        private val TAG = MainPreferenceFragment::class.java.simpleName
    }

    //endregion Nested classes

    //region Fields

    private val developerPreferencesViewModel by lazy {
        ViewModelProviders.of(this).get(DeveloperPreferencesViewModel::class.java)
    }

    private val developerOptionsPreference by lazy {
        findPreference(BuildConfig.PREFS_KEY_DEV_OPTS)
    }

    private val aboutPreference by lazy {
        findPreference(BuildConfig.PREFS_KEY_ABOUT)
    }

    /*
    private val passwordPreference by lazy {
        findPreference(BuildConfig.PREFS_KEY_DEV_PASSWORD_HASH) as PasswordPreference
    }
    */

    //endregion Fields

    //region Inherited methods

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_main, rootKey)
        requireActivity().title = preferenceScreen.title
        aboutPreference.onPreferenceClickListener = this
        developerOptionsPreference.onPreferenceClickListener = this

        with (developerPreferencesViewModel) {
            appVersion.observe(
                    this@MainPreferenceFragment,
                    Observer { version ->
                        aboutPreference.summary = getString(R.string.prefs_about_summary, version)
                    })

            developerOptionsState.observe(
                    this@MainPreferenceFragment,
                    Observer { state ->
                        onDeveloperOptionsStateChange(state) })

            stepsToUnlockDeveloperMode.observe(
                    this@MainPreferenceFragment,
                    Observer { steps ->
                        onStepsToUnlockDeveloperModeChange(steps ?: 0) })

            redundantUnlockRequest.observe(
                    this@MainPreferenceFragment,
                    Observer { onRedundantUnlockRequest() })

            // Force this to add or remove developer options preference now so we don't see
            // the animation as the fragment is created
            onDeveloperOptionsStateChange(developerOptionsState.value)
        }
    }

    //endregion Inherited methods

    //region Implemented methods

    /* Preference.OnPreferenceClickListener */
    override fun onPreferenceClick(preference: Preference?): Boolean {
        return when (preference) {
            aboutPreference -> {
                developerPreferencesViewModel.requestUnlockDeveloperMode()
                false
            }
            developerOptionsPreference -> {
                val extras = Bundle()
                extras.putSerializable(EXTRA_PREFERENCES_TYPE, PreferencesType.DEVELOPER_OPTIONS)
                val intent = Intent(ACTION_SETTINGS)
                intent.putExtras(extras)
                startActivity(intent)
                true
            }
            else -> {
                false
            }
        }
    }

    //endregion Implemented methods

    //region Private methods

    private fun onDeveloperOptionsStateChange(state: DeveloperOptionsState?) {
        when (state) {
            DeveloperOptionsState.UNLOCKED ->
                preferenceScreen?.addPreference(developerOptionsPreference)
            else -> preferenceScreen?.removePreference(developerOptionsPreference)
        }
    }

    private fun onRedundantUnlockRequest() {
        Toast.makeText(
                context,
                R.string.prefs_dev_opts_redundant_unlock_request,
                Toast.LENGTH_SHORT)
                .show()
    }

    private fun onStepsToUnlockDeveloperModeChange(steps: Int) {
        if (steps in 1..STEPS_TO_UNLOCK_SHOW_TOAST) {
            Toast.makeText(
                    context,
                    getString(R.string.prefs_dev_opts_steps_from_unlocking, steps),
                    Toast.LENGTH_SHORT)
                    .show()
        }
    }

    //endregion Private methods
}