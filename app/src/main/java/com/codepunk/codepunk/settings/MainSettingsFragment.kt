package com.codepunk.codepunk.settings

import android.content.Intent
import android.os.Bundle
import android.support.v7.preference.Preference
import android.widget.Toast
import com.codepunk.codepunk.R
import com.codepunk.codepunk.util.EXTRA_SETTINGS_TYPE

class MainSettingsFragment : BaseSettingsFragment() {

    //region Nested classes

    companion object {
        private const val DEVELOPER_CLICK_COUNT_UNLOCK: Int = 7
        private const val DEVELOPER_CLICK_COUNT_TOAST: Int = 4
        private val KEY_DEVELOPER_CLICK_COUNT: String =
                MainSettingsFragment::class.java.name + ".DEVELOPER_CLICK_COUNT"
    }

    //endregion Nested classes

    //region Fields

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
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_DEVELOPER_CLICK_COUNT, developerClickCount)
    }

    //endregion Lifecycle methods

    //region Inherited methods

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_main, rootKey)

        // Trigger lazy instantiation
        developerOptionsPreference.onPreferenceClickListener = this
        versionPreference.summary = getString(
                R.string.settings_version_summary,
                activity?.packageManager?.getPackageInfo(activity?.packageName, 0)?.versionName)
        versionPreference.onPreferenceClickListener = this

        developer = preferenceManager.sharedPreferences.getBoolean(developerPreferenceKey, false)
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        return when (preference) {
            versionPreference -> {
                developerClickCount++
                var toastText: CharSequence? = null
                when {
                    developer -> {
                        toastText = getString(R.string.settings_already_developer)
                    }
                    developerClickCount == DEVELOPER_CLICK_COUNT_UNLOCK -> {
                        toastText = getString(R.string.settings_now_developer)
                        developer = true
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
}
