package com.codepunk.codepunk.settings

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.support.v7.preference.Preference
import android.support.v7.preference.Preference.OnPreferenceClickListener
import android.support.v7.preference.PreferenceFragmentCompat
import com.codepunk.codepunk.R

abstract class BaseSettingsFragment:
        PreferenceFragmentCompat(),
        OnPreferenceClickListener,
        OnSharedPreferenceChangeListener
{
    //region Fields

    protected val apiEnvironmentPreferenceKey: String by lazy {
        getString(R.string.preference_key_api_environment)
    }

    protected val developerPreferenceKey: String by lazy {
        getString(R.string.preference_key_developer)
    }

    protected val developerOptionsPreferenceKey: String by lazy {
        getString(R.string.preference_key_developer_options)
    }

    protected val versionPreferenceKey: String by lazy {
        getString(R.string.preference_key_version)
    }

    //endregion Fields

    //region Lifecycle methods

    override fun onStart() {
        super.onStart()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onStop() {
        super.onStop()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    //endregion Lifecycle methods

    //region Implemented methods

    override fun onPreferenceClick(preference: Preference?): Boolean {
        return false
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
    }

    //endregion Implemented methods
}