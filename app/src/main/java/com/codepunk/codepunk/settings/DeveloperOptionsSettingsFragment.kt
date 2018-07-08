package com.codepunk.codepunk.settings

import android.app.Dialog
import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatDialogFragment
import android.support.v7.preference.ListPreference
import android.support.v7.preference.Preference
import android.util.Log
import com.codepunk.codepunk.BuildConfig.DEFAULT_API_ENVIRONMENT
import com.codepunk.codepunk.R
import com.codepunk.codepunk.api.environment.ApiEnvironment
import com.codepunk.codepunk.util.enumValueOf
import com.codepunk.codepunk.util.populate
import android.content.Intent
import android.service.autofill.Validators.and
import com.codepunk.codepunk.MainActivity
import android.support.v4.content.ContextCompat.startActivity
import android.content.pm.PackageManager
import com.codepunk.codepunk.util.startLaunchActivity


class DeveloperOptionsSettingsFragment : BaseSettingsFragment(), OnClickListener {

    //region Inner classes

    class ConfirmDialogFragment: AppCompatDialogFragment() {
        companion object {
            val FRAGMENT_TAG =
                    ConfirmDialogFragment::class.java.simpleName + ".FRAGMENT_TAG"

            @JvmStatic
            fun newInstance(listener: OnClickListener? = null): ConfirmDialogFragment =
                    ConfirmDialogFragment().apply {
                        onClickListener = listener
                    }
        }

        var onClickListener: OnClickListener? = null

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return AlertDialog.Builder(context!!)
                    .setTitle(R.string.settings_developer_options_title)
                    .setMessage(R.string.settings_confirm_disable_developer_options)
                    .setPositiveButton(R.string.settings_disable_developer_options_positive_button, onClickListener)
                    .setNegativeButton(android.R.string.cancel, onClickListener)
                    .create()
        }
    }

    //endregion Inner classes

    //region Fields

    private val apiEnvironmentPreference: ListPreference by lazy {
        preferenceManager.findPreference(apiEnvironmentPreferenceKey) as ListPreference
    }

    private val disableDeveloperOptionsPreference: Preference by lazy {
        findPreference(disableDeveloperOptionsPreferenceKey)
    }

    //endregion Fields

    //region Lifecycle methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (fragmentManager!!.findFragmentByTag(
                ConfirmDialogFragment.FRAGMENT_TAG) as ConfirmDialogFragment?)
                ?.onClickListener = this
    }

    //endregion Lifecycle methods

    //region Inherited methods

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_developer_options, rootKey)
        disableDeveloperOptionsPreference.onPreferenceClickListener = this

        apiEnvironmentPreference.populate(
                enumClass = ApiEnvironment::class.java,
                entry = { entry(it) })

        onSharedPreferenceChanged(preferenceManager.sharedPreferences, apiEnvironmentPreferenceKey)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            apiEnvironmentPreferenceKey -> {
                val apiEnvironmentName = preferenceManager.sharedPreferences.getString(
                        key,
                        DEFAULT_API_ENVIRONMENT.name)
                val apiEnvironment = enumValueOf(apiEnvironmentName, DEFAULT_API_ENVIRONMENT)
                apiEnvironmentPreference.summary =
                        entry(apiEnvironment) ?: apiEnvironmentName
            }
        }
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        return when (preference) {
            disableDeveloperOptionsPreference -> {
                val ft = fragmentManager!!.beginTransaction()
                val prev = fragmentManager!!.findFragmentByTag(
                        DeveloperOptionsPasscodeDialogFragment.FRAGMENT_TAG)
                if (prev != null) {
                    ft.remove(prev)
                }
                ft.addToBackStack(null)

                // Create and show the dialog.
                ConfirmDialogFragment.newInstance(this)
                        .show(ft, ConfirmDialogFragment.FRAGMENT_TAG)
                true
            }
            else -> {
                super.onPreferenceClick(preference)
            }
        }
    }

    //endregion Inherited methods

    //region Implemented methods
    override fun onClick(dialog: DialogInterface?, which: Int) {
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                val editor = preferenceManager.sharedPreferences.edit()
                editor.remove(validatedDeveloperOptionsPasscodeHashKey)
                val currentEnvironment = ApiEnvironment.valueOf(preferenceManager.sharedPreferences.getString(apiEnvironmentPreferenceKey, ApiEnvironment.PROD.name))
                if (currentEnvironment == ApiEnvironment.PROD) {
                    context?.startLaunchActivity(
                            Intent.FLAG_ACTIVITY_CLEAR_TOP.and(Intent.FLAG_ACTIVITY_SINGLE_TOP))
                } else {
                    editor.putString(apiEnvironmentPreferenceKey, ApiEnvironment.PROD.name)
                }
                editor.apply()
            }
            DialogInterface.BUTTON_NEGATIVE -> {
                // No action
            }
        }
    }

    //endregion Implemented methods

    //region Private methods

    private fun entry(apiEnvironment: ApiEnvironment): CharSequence? {
        return context?.getString(apiEnvironment.nameResId)
    }

    //endregion Private methods
}
