package com.codepunk.codepunk.preferences.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.preference.ListPreference
import android.support.v7.preference.Preference
import com.codepunk.codepunk.BuildConfig
import com.codepunk.codepunk.R
import com.codepunk.codepunk.api.environment.ApiEnvironment
import com.codepunk.codepunk.preferences.viewmodel.DeveloperPreferencesViewModel
import com.codepunk.codepunk.util.populate
import com.codepunk.codepunk.util.startLaunchActivity
import com.codepunk.codepunklibstaging.preference.DeveloperModePreference.DeveloperState
import com.codepunk.codepunklibstaging.preference.ExtendedPreferenceFragmentCompat

class DeveloperOptionsPreferenceFragment:
        ExtendedPreferenceFragmentCompat(),
        Preference.OnPreferenceClickListener {

    //region Nested classes

    companion object {
        private val TAG = DeveloperOptionsPreferenceFragment::class.java.simpleName
    }

    //endregion Nested classes

    //region Fields

    private val developerPreferencesViewModel by lazy {
        ViewModelProviders.of(this).get(DeveloperPreferencesViewModel::class.java)
    }

    private val apiEnvironmentPreference by lazy {
        findPreference(BuildConfig.PREF_KEY_API_ENVIRONMENT) as ListPreference
    }

    private val disableDeveloperOptionsPreference by lazy {
        findPreference(BuildConfig.PREF_KEY_DISABLE_DEV_OPTS)
    }

    //endregion Fields

    //region Inherited methods

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_developer_options, rootKey)
        requireActivity().title = preferenceScreen.title

        apiEnvironmentPreference.populate(
                enumClass = ApiEnvironment::class.java,
                entry = { apiEnvironment -> requireContext().getString(apiEnvironment.nameResId) })

        disableDeveloperOptionsPreference.onPreferenceClickListener = this

        developerPreferencesViewModel.developerState.observe(
                this,
                Observer { state ->
                    onDeveloperStateChange(state ?: DeveloperState.NOT_DEVELOPER) })
    }

    /*
    override fun onDisplayPreferenceDialog(preference: Preference?) {
        when (preference) {
            disableDeveloperOptionsPreference -> {
                val fragment = TestFragment.newInstance(disableDeveloperOptionsPreference)
                fragment.setTargetFragment(this, 0)
//                fragment.show(requireFragmentManager(), "android.support.v7.preference.PreferenceFragment.DIALOG")
                fragment.show(requireFragmentManager(), TestFragment.FRAGMENT_TAG)
            }
            else -> {
                super.onDisplayPreferenceDialog(preference)
            }
        }
//        when (preference) {
//            disableDeveloperOptionsPreference -> {
//                val fragment = TestFragment.newInstance(preference?.key ?: "")
//                fragment.setTargetFragment(this, 0)
//                fragment.show(requireFragmentManager(), "android.support.v7.preference.PreferenceFragment.DIALOG")
//            }
//            else -> { super.onDisplayPreferenceDialog(preference) }
//        }
    }
    */

    //endregion Inherited methods

    //region Implemented methods

    override fun onPreferenceClick(preference: Preference?): Boolean {
        return when (preference) {
            disableDeveloperOptionsPreference -> {
                // TODO Show confirm disable fragment
//                val fragment = ConfirmDialogFragment.newInstance()
                /*.also {
                    it.alertDialog?.apply {
                        setTitle(R.string.pref_dev_confirm_disable_developer_options_title)
                        setMessage(getString(R.string.pref_dev_confirm_disable_developer_options_message))
                        setButton(
                                DialogInterface.BUTTON_POSITIVE,
                                getString(R.string.pref_dev_confirm_disable_developer_options_positive_button)
                        ) { _, _ -> }
                    }
                }
                */
//                val transaction = requireFragmentManager().beginTransaction().addToBackStack(null)
//                fragment.show(transaction, ConfirmDialogFragment.FRAGMENT_TAG)

//                developerPreferencesViewModel.unregisterDeveloper()
                true
            }
            else -> {
                false
            }
        }
    }

    //endregion Implemented methods

    //region Private methods

    private fun onDeveloperStateChange(state: DeveloperState) {
        when(state) {
            DeveloperState.NOT_DEVELOPER -> {
                // Option 1: Just go back
                // requireActivity().finish()

                // Option 2: Go all the way back to launch activity
                // TODO: Will want a confirmation dialog first
                requireContext().startLaunchActivity(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            else -> { /* No action */ }
        }
    }
}
