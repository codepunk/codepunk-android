package com.codepunk.codepunk

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.Preference
import android.support.v7.preference.Preference.OnPreferenceClickListener
import android.support.v7.preference.PreferenceFragmentCompat
import android.util.Log
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
            private const val PREF_KEY_VERSION = "key_version"
        }

        //endregion Nested classes

        //region Fields

        private var _developer = false
        private var developer
            get() = _developer
            set(value) {
                _developer = value
                createPreferences()
            }

        private var versionPreference: Preference? = null
            set(value) {
                field = value
                value?.summary = getString(R.string.summary_version, versionName)
                value?.onPreferenceClickListener = this
            }

        private val versionName: String? by lazy {
            activity?.packageManager?.getPackageInfo(activity?.packageName, 0)?.versionName
        }

        private var developerClickCount: Int = 0

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
            createPreferences(rootKey)
        }

        //endregion Inherited methods

        //region Implemented methods

        override fun onPreferenceClick(preference: Preference?): Boolean {
            developerClickCount++
            var toastText: CharSequence? = null
            when {
                developerClickCount > DEVELOPER_CLICK_COUNT_UNLOCK -> {
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

        //region Private methods

        private fun createPreferences(rootKey: String? = null) {
            setPreferencesFromResource(
                    if (developer) R.xml.preferences_main_developer else R.xml.preferences_main,
                    rootKey)
            versionPreference = findPreference(PREF_KEY_VERSION)
            Log.d("Test", versionPreference?.toString() ?: "versionPreference is null")
        }

        //endregion Private methods
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
