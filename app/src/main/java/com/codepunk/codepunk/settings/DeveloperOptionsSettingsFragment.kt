package com.codepunk.codepunk.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.ListPreference
import android.support.v7.preference.PreferenceFragmentCompat
import com.codepunk.codepunk.R
import com.codepunk.codepunk.api.environment.ApiEnvironment
import com.codepunk.codepunk.util.PreferenceKey.API_ENVIRONMENT
import com.codepunk.codepunk.util.getKey

class DeveloperOptionsSettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    //region Fields

    private val apiEnvironmentPreference: ListPreference by lazy {
        val pref = preferenceManager.findPreference(
                preferenceManager.getKey(API_ENVIRONMENT)) as ListPreference
        val initialCapacity = ApiEnvironment.values().size
        val entryValues: ArrayList<CharSequence> = ArrayList(initialCapacity)
        val entries: ArrayList<CharSequence> = ArrayList(initialCapacity)
        for (apiEnvironment in ApiEnvironment.values()) {
            entryValues.add(apiEnvironment.name)
            entries.add(context?.getString(apiEnvironment.nameResId)!!)
        }
        pref.entryValues = entryValues.toTypedArray()
        pref.entries = entries.toTypedArray()
        pref
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

    //region Inherited methods

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_developer_options, rootKey)
        onSharedPreferenceChanged(
                preferenceManager.sharedPreferences,
                preferenceManager.getKey(API_ENVIRONMENT))
    }

    //endregion Inherited methods

    //region Implemented methods

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            preferenceManager.getKey(API_ENVIRONMENT) -> {
                val apiEnvironmentName = preferenceManager.sharedPreferences.getString(
                        key,
                        ApiEnvironment.PROD.name)
                val apiEnvironment = ApiEnvironment.valueOf(apiEnvironmentName)
                apiEnvironmentPreference.summary =
                        context?.getString(apiEnvironment.nameResId) ?: "Unknown" /* TODO */
            }
        }
    }

    //endregion Implemented methods
}
