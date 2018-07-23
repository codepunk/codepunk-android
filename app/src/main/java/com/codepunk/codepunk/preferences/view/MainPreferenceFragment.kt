package com.codepunk.codepunk.preferences.view

import android.app.Activity.RESULT_OK
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.preference.Preference
import android.util.Log
import android.widget.Toast
import com.codepunk.codepunk.BuildConfig
import com.codepunk.codepunk.R
import com.codepunk.codepunk.developer.DeveloperPasswordDialogFragment
import com.codepunk.codepunk.preferences.PreferencesActivity
import com.codepunk.codepunk.preferences.PreferencesActivity.PreferencesType
import com.codepunk.codepunk.preferences.viewmodel.DeveloperPreferencesViewModel
import com.codepunk.codepunk.util.EXTRA_DEVELOPER_PASSWORD_HASH
import com.codepunk.codepunklib.preference.DialogDelegatePreferenceFragment
import com.codepunk.codepunklibstaging.preference.SwitchTwoTargetPreference

// region Constants

private const val DEVELOPER_PASSWORD_DIALOG_FRAGMENT_REQUEST_CODE = 0

private const val DEFAULT_STEPS_REMAINING: Int = 7
private const val STEPS_TO_SHOW_TOAST = 3
private const val SAVE_STATE_STEPS_REMAINING = "stepsRemaining"

// endregion Constants

class MainPreferenceFragment:
        DialogDelegatePreferenceFragment(),
        Preference.OnPreferenceChangeListener,
        Preference.OnPreferenceClickListener {

    // region Nested classes

    companion object {
        private val TAG = MainPreferenceFragment::class.java.simpleName

        private val DEVELOPER_PASSWORD_DIALOG_FRAGMENT_TAG =
                MainPreferenceFragment::class.java.name + ".DEVELOPER_PASSWORD_DIALOG"
    }

    // endregion Nested classes

    // region Fields

    private val preferencesActivity by lazy {
        requireActivity() as? PreferencesActivity
                ?: throw IllegalStateException("Activity must be an instance of " +
                        PreferencesActivity::class.java.simpleName)
    }

    private var stepsRemaining = 0 // TODO Rename?

    private val developerPreferencesViewModel by lazy {
        ViewModelProviders.of(this).get(DeveloperPreferencesViewModel::class.java)
    }

    private val developerOptionsPreference by lazy {
        findPreference(BuildConfig.PREFS_KEY_DEV_OPTS_ENABLED) as SwitchTwoTargetPreference
    }

    private val aboutPreference by lazy {
        findPreference(BuildConfig.PREFS_KEY_ABOUT)
    }

    // endregion Fields

    // region Lifecycle methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stepsRemaining = when {
            savedInstanceState != null ->
                savedInstanceState.getInt(SAVE_STATE_STEPS_REMAINING, DEFAULT_STEPS_REMAINING)
            developerPreferencesViewModel.developerOptionsUnlocked.value == true -> 0
            else -> DEFAULT_STEPS_REMAINING
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SAVE_STATE_STEPS_REMAINING, stepsRemaining)
    }

    // endregion Lifecycle methods

    // region Inherited methods

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            DEVELOPER_PASSWORD_DIALOG_FRAGMENT_REQUEST_CODE -> {
                when (resultCode) {
                    RESULT_OK -> {
                        data?.run {
                            developerPreferencesViewModel.enableDeveloperOptions(
                                    getStringExtra(EXTRA_DEVELOPER_PASSWORD_HASH))
                        }
                    }
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_main, rootKey)
        requireActivity().title = preferenceScreen.title
        aboutPreference.onPreferenceClickListener = this
        developerOptionsPreference.onPreferenceClickListener = this
        developerOptionsPreference.onPreferenceChangeListener = this

        with (developerPreferencesViewModel) {
            appVersion.observe(
                    this@MainPreferenceFragment,
                    Observer { version ->
                        aboutPreference.summary = getString(R.string.prefs_about_summary, version)
                    })

            developerOptionsAuthenticatedHash.observe(
                    this@MainPreferenceFragment,
                    Observer { hash -> onAuthenticatedDeveloperHashChange(hash) })

            developerOptionsEnabled.observe(
                    this@MainPreferenceFragment,
                    Observer { enabled ->
                        onDeveloperOptionsEnabledChange(enabled == true)
                    })

            developerOptionsUnlocked.observe(
                    this@MainPreferenceFragment,
                    Observer { unlocked ->
                        onDeveloperOptionsUnlockedChange(unlocked == true)
                    })
        }
    }

    // endregion Inherited methods

    // region Implemented methods

    // Preference.OnPreferenceChangeListener
    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        return when (preference) {
            developerOptionsPreference -> {
                val enabled = newValue as Boolean
                if (enabled) {
                    showDeveloperPasswordDialogFragment()
                    false
                } else {
                    // TODO Dialog to ask if user is sure
                    // TODO TEMP
                    developerPreferencesViewModel.unlockDeveloperOptions()
                    // END TEMP
                    true
                }
            }
            else -> { true }
        }
    }

    // Preference.OnPreferenceClickListener
    override fun onPreferenceClick(preference: Preference?): Boolean {
        return when (preference) {
            aboutPreference -> {
                when {
                    stepsRemaining > 1 -> {
                        stepsRemaining--
                        onStepsToShowDeveloperOptionsChange(stepsRemaining)
                    }
                    stepsRemaining == 1 -> {
                        stepsRemaining = 0
                        developerPreferencesViewModel.unlockDeveloperOptions()
                    }
                    else -> onRedundantShowRequest()
                }
                false
            }
            developerOptionsPreference -> {
                preferencesActivity.startPreferencesActivity(PreferencesType.DEVELOPER_OPTIONS)
                true
            }
            else -> {
                false
            }
        }
    }

    // endregion Implemented methods

    // region Private methods

    private fun onAuthenticatedDeveloperHashChange(hash: String?) {
        Log.d(TAG, "onAuthenticatedDeveloperHashChange!!! hash=$hash")
    }

    private fun onDeveloperOptionsEnabledChange(enabled: Boolean) {
        developerOptionsPreference.isChecked = enabled
    }

    private fun onDeveloperOptionsUnlockedChange(unlocked: Boolean) {
        preferenceScreen.apply {
            if (unlocked) {
                addPreference(developerOptionsPreference)
            } else {
                removePreference(developerOptionsPreference)
            }
        }
    }

    private fun onRedundantShowRequest() {
        Toast.makeText(
                context,
                R.string.prefs_dev_opts_redundant_show_request,
                Toast.LENGTH_SHORT)
                .show()
    }

    private fun onStepsToShowDeveloperOptionsChange(steps: Int) {
        if (steps in 1..STEPS_TO_SHOW_TOAST) {
            Toast.makeText(
                    context,
                    getString(R.string.prefs_dev_opts_steps_from_unlocking, steps),
                    Toast.LENGTH_SHORT)
                    .show()
        }
    }

    private fun showDeveloperPasswordDialogFragment() {
        with (requireFragmentManager()) {
            if (findFragmentByTag(DEVELOPER_PASSWORD_DIALOG_FRAGMENT_TAG) != null) {
                return
            }

            DeveloperPasswordDialogFragment.newInstance()
                    .apply { setTargetFragment(
                            this@MainPreferenceFragment,
                            DEVELOPER_PASSWORD_DIALOG_FRAGMENT_REQUEST_CODE) }
                    .show(this, DEVELOPER_PASSWORD_DIALOG_FRAGMENT_TAG)
        }
    }

    // endregion Private methods
}