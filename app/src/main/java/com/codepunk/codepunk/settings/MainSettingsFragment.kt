package com.codepunk.codepunk.settings

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.preference.Preference
import android.widget.Toast
import com.codepunk.codepunk.BuildConfig
import com.codepunk.codepunk.R
import com.codepunk.codepunk.util.EXTRA_SETTINGS_TYPE
import com.codepunk.codepunk.settings.DeveloperOptionsPasscodeDialogFragment.OnPasscodeResultListener

class MainSettingsFragment : BaseSettingsFragment(), OnPasscodeResultListener {

    //region Nested classes

    companion object {
        private const val DEVELOPER_CLICK_COUNT_UNLOCK: Int = 7
        private const val DEVELOPER_CLICK_COUNT_TOAST: Int = 4
        private val KEY_DEVELOPER_CLICK_COUNT: String =
                MainSettingsFragment::class.java.name + ".DEVELOPER_CLICK_COUNT"
    }

    //endregion Nested classes

    //region Fields

    /*
    private var developer = false
        set(value) {
            field = value
            preferenceManager.sharedPreferences
                    .edit()
                    .putBoolean(developerPreferenceKey, value)
                    .apply()
            if (value) {
                preferenceScreen.addPreference(developerOptionsPreference)
            } else {
                preferenceScreen.removePreference(developerOptionsPreference)
            }
        }
    */

    private var developerClickCount: Int = 0

    private val developerOptionsPreference: Preference by lazy {
        findPreference(developerOptionsPreferenceKey)
    }

    private val versionPreference: Preference by lazy {
        findPreference(versionPreferenceKey)
    }

    //endregion Fields

    //region Lifecycle methods

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        developerClickCount = savedInstanceState?.getInt(KEY_DEVELOPER_CLICK_COUNT) ?: 0

        val passcodeFragment: DeveloperOptionsPasscodeDialogFragment? =
                fragmentManager!!.findFragmentByTag(
                        DeveloperOptionsPasscodeDialogFragment.FRAGMENT_TAG)
                        as DeveloperOptionsPasscodeDialogFragment?
        passcodeFragment?.onPasscodeResultListener = this

        if (preferenceManager.sharedPreferences.contains(validatedDeveloperOptionsPasscodeHashKey)) {
            val passcode =  preferenceManager.sharedPreferences.getString(validatedDeveloperOptionsPasscodeHashKey, "")
            if (passcode == BuildConfig.DEVELOPER_OPTIONS_PASSCODE_HASH) {
                // Add the developer options preference back in
                preferenceScreen.addPreference(developerOptionsPreference)
            } else if (savedInstanceState == null) {
                // Open dialog with special "changed" message. Maybe UPDATE if already visible?
                showDialogWithMessage(R.string.settings_developer_options_passcode_dialog_message_changed)
            }
        } else {
            // No action necessary; user hasn't enabled developer options
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_DEVELOPER_CLICK_COUNT, developerClickCount)
    }

    //endregion Lifecycle methods

    //region Inherited methods

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_main, rootKey)
        preferenceScreen.removePreference(developerOptionsPreference)

        developerOptionsPreference.onPreferenceClickListener = this
        versionPreference.summary = getString(
                R.string.settings_version_summary,
                activity?.packageManager?.getPackageInfo(activity?.packageName, 0)?.versionName)
        versionPreference.onPreferenceClickListener = this

// TODO        developer = preferenceManager.sharedPreferences.getBoolean(developerPreferenceKey, false)
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        return when (preference) {
            versionPreference -> {
                developerClickCount++
                var toastText: CharSequence? = null
                when {
// TODO
//                    developer -> {
//                        toastText = getString(R.string.settings_already_developer)
//                    }
                    developerClickCount == DEVELOPER_CLICK_COUNT_UNLOCK -> {
//                        toastText = getString(R.string.settings_now_developer)
                        showDialogWithMessage(R.string.settings_developer_options_passcode_dialog_message)
                    }
                    developerClickCount >= DEVELOPER_CLICK_COUNT_TOAST -> {
                        toastText = getString(
                                R.string.settings_steps_from_developer,
                                DEVELOPER_CLICK_COUNT_UNLOCK - developerClickCount)
                    }
                }
                if (toastText != null) {
                    Toast.makeText(
                            context,
                            toastText,
                            Toast.LENGTH_SHORT)
                            .show()
                }
                true
            }
            developerOptionsPreference -> {
                val extras = Bundle()
                extras.putSerializable(EXTRA_SETTINGS_TYPE, SettingsType.DEVELOPER_OPTIONS)
                val intent = Intent(context, SettingsActivity::class.java)
                intent.putExtras(extras)
                startActivity(intent)
                true
            }
            else -> {
                super.onPreferenceClick(preference)
            }
        }
    }

    //endregion Inherited methods

    //region Implemented methods

    override fun onCancel(dialog: DialogInterface?) {
        preferenceManager.sharedPreferences
                .edit()
                .remove(validatedDeveloperOptionsPasscodeHashKey)
                .apply()
        developerClickCount = 0
        // TODO Put in setter
    }

    override fun onPasscodeSuccess(passcode: String, hash:String) {
        // TODO Send the actual hash in the method and don't use BuildConfig
        preferenceManager.sharedPreferences
                .edit()
                .putString(validatedDeveloperOptionsPasscodeHashKey, hash)
                .apply()
        preferenceScreen.addPreference(developerOptionsPreference)
        // TODO Put in setter
    }

    //endregion Implemented methods

    //region Private methods

    // TODO TEMP

    private fun showDialogWithMessage(@StringRes resId: Int) {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        val ft = fragmentManager!!.beginTransaction()
        val prev = fragmentManager!!.findFragmentByTag(DeveloperOptionsPasscodeDialogFragment.FRAGMENT_TAG)
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)

        // Create and show the dialog.
        val newFragment = DeveloperOptionsPasscodeDialogFragment.newInstance(resId, this)
        newFragment.show(ft, DeveloperOptionsPasscodeDialogFragment.FRAGMENT_TAG)
    }

    // END TEMP

    //endregion Private methods
}
