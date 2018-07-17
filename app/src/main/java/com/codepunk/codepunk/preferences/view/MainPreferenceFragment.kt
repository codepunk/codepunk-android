package com.codepunk.codepunk.preferences.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.preference.Preference
import android.util.Log
import android.widget.Toast
import com.codepunk.codepunk.BuildConfig
import com.codepunk.codepunk.R
import com.codepunk.codepunk.preferences.PreferencesActivity.PreferencesType
import com.codepunk.codepunk.preferences.viewmodel.DeveloperPreferencesViewModel
import com.codepunk.codepunk.util.ACTION_SETTINGS
import com.codepunk.codepunk.util.EXTRA_PREFERENCES_TYPE
import com.codepunk.codepunklibstaging.preference.DeveloperModePreference
import com.codepunk.codepunklibstaging.preference.DeveloperModePreference.DeveloperState
import com.codepunk.codepunklibstaging.preference.ExtendedPreferenceFragmentCompat
import com.codepunk.codepunklibstaging.preference.PasswordPreference

class MainPreferenceFragment:
        ExtendedPreferenceFragmentCompat(),
        Preference.OnPreferenceClickListener,
/*        DeveloperPasswordDialogFragment.OnPasswordResultListener, */
        DeveloperModePreference.OnRemainingClicksChangeListener {

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
        findPreference(BuildConfig.PREF_KEY_DEV_OPTS)
    }

    private val developerModePreferenceOld by lazy {
        findPreference(BuildConfig.PREF_KEY_DEV_PASSWORD_HASH_OLD) as DeveloperModePreference
    }

    private val passwordPreference by lazy {
        findPreference(BuildConfig.PREF_KEY_DEV_PASSWORD_HASH) as PasswordPreference
    }

    /*
    private val developerPasswordDialogFragment: DeveloperPasswordDialogFragment?
    get() = requireFragmentManager().findFragmentByTag(
            DeveloperPasswordDialogFragment.FRAGMENT_TAG) as DeveloperPasswordDialogFragment?
    */

    //endregion Fields

    //region Lifecycle methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Force this method now to avoid visual artifacts
        onDeveloperStateChange(
                developerPreferencesViewModel.developerState.value ?: DeveloperState.NOT_DEVELOPER)

        /*
        developerPasswordDialogFragment?.apply {
            onPasswordResultListener = this@MainPreferenceFragment
        }
        */
    }

    override fun onStart() {
        super.onStart()

        // Force this method now to avoid visual artifacts
        //val state = developerPreferencesViewModel.developerState.value ?: DeveloperState.NOT_DEVELOPER
        //onDeveloperStateChange(state)

        developerPreferencesViewModel.refreshState()
    }

    //endregion Lifecycle methods

    //region Inherited methods

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_main, rootKey)
        requireActivity().title = preferenceScreen.title
        developerOptionsPreference.onPreferenceClickListener = this
        developerModePreferenceOld.onPreferenceClickListener = this
        developerModePreferenceOld.onRemainingClicksChangeListener = this
        passwordPreference.onPreferenceClickListener = this

        with (developerPreferencesViewModel) {
            appVersion.observe(
                    this@MainPreferenceFragment,
                    Observer { version ->
                        val summary = getString(R.string.pref_about_summary, version)
                        developerModePreferenceOld.summary = summary
                        passwordPreference.summary = summary
                    })

            persistedPasswordHash.observe(
                    this@MainPreferenceFragment,
                    Observer {
                        // Check if stale
                        if (it != null && !BuildConfig.DEVELOPER_PASSWORD_HASH.equals(it, true)) {
                            onStaleDeveloperPassword()
                        }
                    })

            developerState.observe(
                    this@MainPreferenceFragment,
                    Observer { state ->
                        onDeveloperStateChange(state ?: DeveloperState.NOT_DEVELOPER) })

            stepsToUnlockDeveloperMode.observe(
                    this@MainPreferenceFragment,
                    Observer { steps ->
                        onStepsToUnlockDeveloperModeChange(steps ?: 0) })

            redundantUnlockRequest.observe(
                    this@MainPreferenceFragment,
                    Observer { onRedundantUnlockRequest() })
        }




        /*
        developerPreferencesViewModel.nStepsFromDeveloper.observe(
                this,
                Observer { steps -> onNStepsFromDeveloper(steps ?: 0) })

        developerPreferencesViewModel.redundantUnlockRequest.observe(
                this,
                Observer { onRedundantUnlockRequest() })
        */
    }

    //endregion Inherited methods

    //region Implemented methods

    /* Preference.OnPreferenceClickListener */
    override fun onPreferenceClick(preference: Preference?): Boolean {
        return when (preference) {
            developerModePreferenceOld -> {
                if (developerModePreferenceOld.isDeveloper) {
                    Toast.makeText(
                            context,
                            R.string.pref_dev_opts_redundant_unlock_request,
                            Toast.LENGTH_SHORT)
                            .show()
                }
                true
            }
            passwordPreference -> {
                if (!developerPreferencesViewModel.unlockDeveloperMode()) {
                    passwordPreference.suppressDialogOnNextClick()
                }
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

    /*
    // DeveloperPasswordDialogFragment.OnPasswordResultListener
    override fun onPasswordFailure(dialog: DialogInterface?, password: String) {
        // No action
    }

    // DeveloperPasswordDialogFragment.OnPasswordResultListener
    override fun onPasswordSuccess(dialog: DialogInterface?, password: String, hash: String) {
        developerPreferencesViewModel.registerDeveloper(hash)
    }
    */

    // DeveloperModePreference.OnRemainingClicksChangeListener
    override fun onRemainingClicksChanged(preference: DeveloperModePreference, remainingClicksToUnlock: Int) {
        if (remainingClicksToUnlock in 1..3) {
            Toast.makeText(
                    context,
                    getString(R.string.pref_dev_opts_steps_from_developer, remainingClicksToUnlock),
                    Toast.LENGTH_SHORT)
                    .show()
        }
    }

    //endregion Implemented methods

    //region Private methods

    private fun onDeveloperStateChange(state: DeveloperState) {
        var showPasswordDialog = false
        var message: CharSequence? = null
        when (state) {
            DeveloperState.NOT_DEVELOPER -> {
                preferenceScreen.removePreference(developerOptionsPreference)
            }
            DeveloperState.DEVELOPER -> {
                preferenceScreen.addPreference(developerOptionsPreference)
            }
/*
            DeveloperState.AWAITING_PASSWORD -> {
                showPasswordDialog = true
                message = getString(R.string.pref_dev_opts_enter_password)
            }
*/
            DeveloperState.STALE_PASSWORD -> {
                showPasswordDialog = true
//                message = getString(R.string.pref_dev_opts_stale_password)
            }
        }

        /*
        if (showPasswordDialog) {
            var fragment = developerPasswordDialogFragment
            if (fragment == null) {
                fragment = DeveloperPasswordDialogFragment.newInstance(this)
                val transaction = requireFragmentManager().beginTransaction().addToBackStack(null)
                fragment.show(transaction, DeveloperPasswordDialogFragment.FRAGMENT_TAG)
            } else {
                fragment.message = message
            }
        }
        */
    }

    private fun onStaleDeveloperPassword() {
        Log.d(TAG, "******************* Made it to stale password! *******************")
        onDisplayPreferenceDialog(developerModePreferenceOld)
    }

    private fun onStepsToUnlockDeveloperModeChange(steps: Int) {
        if (steps in 1..3) {
            Toast.makeText(
                    context,
                    getString(R.string.pref_dev_opts_steps_from_developer, steps),
                    Toast.LENGTH_SHORT)
                    .show()
        }
    }

    private fun onRedundantUnlockRequest() {
        Toast.makeText(
                context,
                R.string.pref_dev_opts_redundant_unlock_request,
                Toast.LENGTH_SHORT)
                .show()
    }

    /*
    private fun onNStepsFromDeveloper(steps: Int) {
        Toast.makeText(
                context,
                getString(R.string.pref_dev_opts_steps_from_developer, steps),
                Toast.LENGTH_SHORT)
                .show()
    }
    */

    //endregion Private methods
}