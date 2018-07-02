package com.codepunk.codepunk

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.Preference
import android.support.v7.preference.Preference.OnPreferenceClickListener
import android.support.v7.preference.PreferenceFragmentCompat
import android.widget.Toast

class SettingsActivity : AppCompatActivity() {

    //region Nested classes

    class SettingsFragment: PreferenceFragmentCompat(), OnPreferenceClickListener {

        //region Nested classes

        companion object {
            private const val DEVELOPER_CLICK_COUNT_UNLOCK: Int = 7
            private const val DEVELOPER_CLICK_COUNT_TOAST: Int = 4
            private val KEY_DEVELOPER_CLICK_COUNT: String =
                    SettingsFragment::class.java.name + ".DEVELOPER_CLICK_COUNT"

            private const val PREF_KEY_DEVELOPER_OPTIONS = "key_developer_options"
            private const val PREF_KEY_VERSION = "key_version"

            private const val PREF_KEY_DEVELOPER = "key_developer"
        }

        //endregion Nested classes

        //region Fields

        private var developer = false
            set(value) {
                field = value
                preferenceManager.sharedPreferences
                        .edit()
                        .putBoolean(PREF_KEY_DEVELOPER, value)
                        .apply()
                if (value) {
                    preferenceScreen.addPreference(developerOptionsPreference)
                } else {
                    preferenceScreen.removePreference(developerOptionsPreference)
                }
            }

        private var developerClickCount: Int = 0

        private val developerOptionsPreference: Preference by lazy {
            findPreference(PREF_KEY_DEVELOPER_OPTIONS)
        }

        private val versionName: String? by lazy {
            activity?.packageManager?.getPackageInfo(activity?.packageName, 0)?.versionName
        }

        private val versionPreference: Preference by lazy {
            findPreference(PREF_KEY_VERSION)
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
            versionPreference.summary = getString(R.string.summary_version, versionName)
            versionPreference.onPreferenceClickListener = this
            developer = preferenceManager.sharedPreferences.getBoolean(PREF_KEY_DEVELOPER, false)
        }

        //endregion Inherited methods

        //region Implemented methods

        override fun onPreferenceClick(preference: Preference?): Boolean {
            developerClickCount++
            var toastText: CharSequence? = null
            when {
                developer -> {
                    toastText = getString(R.string.already_developer)
                }
                developerClickCount == DEVELOPER_CLICK_COUNT_UNLOCK -> {
                    toastText = getString(R.string.now_developer)
                    developer = true
                }
                developerClickCount >= DEVELOPER_CLICK_COUNT_TOAST -> {
                    toastText = getString(
                            R.string.steps_from_developer,
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
            return true
        }

        //endregion Implemented methods
    }

    //endregion Nested classes

    //region Lifecycle methods

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (supportFragmentManager.findFragmentById(android.R.id.content) == null) {
            supportFragmentManager
                    .beginTransaction()
                    .add(android.R.id.content, SettingsFragment())
                    .commit()
        }
    }

    //endregion Lifecycle methods
}
