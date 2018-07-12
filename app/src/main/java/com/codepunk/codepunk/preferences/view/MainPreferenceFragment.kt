package com.codepunk.codepunk.preferences.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.widget.Toast
import com.codepunk.codepunk.BuildConfig
import com.codepunk.codepunk.R
import com.codepunk.codepunk.preferences.DeveloperPasswordDialogFragment
import com.codepunk.codepunk.preferences.PreferencesType
import com.codepunk.codepunk.preferences.viewmodel.DeveloperPreferencesViewModel
import com.codepunk.codepunk.preferences.viewmodel.DeveloperPreferencesViewModel.DeveloperState
import com.codepunk.codepunk.util.ACTION_SETTINGS
import com.codepunk.codepunk.util.EXTRA_PREFERENCES_TYPE

class MainPreferenceFragment:
        PreferenceFragmentCompat(),
        Preference.OnPreferenceClickListener,
        DeveloperPasswordDialogFragment.OnPasswordResultListener {

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

    private val aboutPreference by lazy {
        findPreference(BuildConfig.PREF_KEY_VERSION)
    }

    private val developerPasswordDialogFragment: DeveloperPasswordDialogFragment?
    get() = requireFragmentManager().findFragmentByTag(
            DeveloperPasswordDialogFragment.FRAGMENT_TAG) as DeveloperPasswordDialogFragment?

    //endregion Fields

    //region Lifecycle methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Force this method now to avoid visual artifacts
        onDeveloperStateChange(
                developerPreferencesViewModel.developerState.value ?: DeveloperState.NOT_DEVELOPER)

        developerPasswordDialogFragment?.apply {
            onPasswordResultListener = this@MainPreferenceFragment
        }
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
        aboutPreference.onPreferenceClickListener = this

        developerPreferencesViewModel.appVersion.observe(
                this,
                Observer { version -> aboutPreference.summary =
                        getString(R.string.pref_about_summary, version) })

        developerPreferencesViewModel.developerState.observe(
                this,
                Observer { state ->
                    onDeveloperStateChange(state ?: DeveloperState.NOT_DEVELOPER) })

        developerPreferencesViewModel.nStepsFromDeveloper.observe(
                this,
                Observer { steps -> onNStepsFromDeveloper(steps ?: 0) })

        developerPreferencesViewModel.redundantUnlockRequest.observe(
                this,
                Observer { onRedundantUnlockRequest() })
    }

    //endregion Inherited methods

    //region Implemented methods

    /* Preference.OnPreferenceClickListener */
    override fun onPreferenceClick(preference: Preference?): Boolean {
        return when (preference) {
            aboutPreference -> {
                developerPreferencesViewModel.unlockDeveloperOptions()
                true
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

    /* DeveloperPasswordDialogFragment.OnPasswordResultListener */
    override fun onPasswordFailure(dialog: DialogInterface?, password: String) {
        // No action
    }

    /* DeveloperPasswordDialogFragment.OnPasswordResultListener */
    override fun onPasswordSuccess(dialog: DialogInterface?, password: String, hash: String) {
        developerPreferencesViewModel.registerDeveloper(hash)
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
            DeveloperState.AWAITING_PASSWORD -> {
                showPasswordDialog = true
                message = getString(R.string.pref_dev_opts_enter_password)
            }
            DeveloperState.STALE_PASSWORD -> {
                showPasswordDialog = true
                message = getString(R.string.pref_dev_opts_stale_password)
            }
        }
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
    }

    private fun onRedundantUnlockRequest() {
        Toast.makeText(
                context,
                R.string.pref_dev_opts_redundant_unlock_request,
                Toast.LENGTH_SHORT)
                .show()
    }

    private fun onNStepsFromDeveloper(steps: Int) {
        Toast.makeText(
                context,
                getString(R.string.pref_dev_opts_steps_from_developer, steps),
                Toast.LENGTH_SHORT)
                .show()
    }

    //endregion Private methods
}