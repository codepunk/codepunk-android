package com.codepunk.codepunk.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.ListPreference
import com.codepunk.codepunk.BuildConfig
import com.codepunk.codepunk.BuildConfig.DEFAULT_API_ENVIRONMENT
import com.codepunk.codepunk.R
import com.codepunk.codepunk.api.environment.ApiEnvironment
import com.codepunk.codepunk.util.populate
import com.codepunk.codepunk.util.enumValueOf

class DeveloperOptionsSettingsFragment : BaseSettingsFragment() {

    //region Fields

    private val apiEnvironmentPreference: ListPreference by lazy {
        preferenceManager.findPreference(apiEnvironmentPreferenceKey) as ListPreference
    }

    //endregion Fields

    //region Inherited methods

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_developer_options, rootKey)
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
        apiEnvironmentPreference.populate(
                enumClass = ApiEnvironment::class.java,
                entry = { entry(it) })
    }

    //endregion Inherited methods

    //region Private methods

    private fun entry(apiEnvironment: ApiEnvironment): CharSequence? {
        return context?.getString(apiEnvironment.nameResId)
    }

    //endregion Private methods
}
