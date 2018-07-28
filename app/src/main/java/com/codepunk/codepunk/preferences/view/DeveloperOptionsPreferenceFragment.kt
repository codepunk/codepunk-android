package com.codepunk.codepunk.preferences.view

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.preference.ListPreference
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import com.codepunk.codepunk.BuildConfig
import com.codepunk.codepunk.R
import com.codepunk.codepunk.api.environment.ApiEnvironment
import com.codepunk.codepunk.preferences.viewmodel.DeveloperPreferencesViewModel
import com.codepunk.codepunk.util.populate

class DeveloperOptionsPreferenceFragment:
        PreferenceFragmentCompat(),
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
        findPreference(BuildConfig.PREFS_KEY_API_ENVIRONMENT) as ListPreference
    }

    /*
    private val disableDeveloperOptionsPreference by lazy {
        findPreference(BuildConfig.PREFS_KEY_DISABLE_DEV_OPTS)
    }
    */

    //endregion Fields

    //region Inherited methods

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_developer_options, rootKey)
        requireActivity().title = preferenceScreen.title

        apiEnvironmentPreference.populate(
                enumClass = ApiEnvironment::class.java,
                entry = { apiEnvironment -> requireContext().getString(apiEnvironment.nameResId) })

        /*
        disableDeveloperOptionsPreference.onPreferenceClickListener = this
        */
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
        return false
        /*
        return when (preference) {
            disableDeveloperOptionsPreference -> {
                // TODO Show confirm disable fragment
//                val fragment = ConfirmDialogFragment.newInstance()
                .also {
                    it.alertDialog?.apply {
                        setTitle(R.string.prefs_dev_confirm_disable_developer_options_title)
                        setMessage(getString(R.string.prefs_dev_confirm_disable_developer_options_message))
                        setButton(
                                DialogInterface.BUTTON_POSITIVE,
                                getString(R.string.prefs_dev_confirm_disable_developer_options_positive_button)
                        ) { _, _ -> }
                    }
                }

//                val transaction = requireFragmentManager().beginTransaction().addToBackStack(null)
//                fragment.show(transaction, ConfirmDialogFragment.FRAGMENT_TAG)

//                developerPreferencesViewModel.unregisterDeveloper()
                true
            }
            else -> {
                false
            }
        }
        */
    }

    //endregion Implemented methods
}
