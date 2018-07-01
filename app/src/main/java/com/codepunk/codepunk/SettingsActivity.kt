package com.codepunk.codepunk

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.util.Log

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_settings)
        //setSupportActionBar(toolbar)

        /*
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        */

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (supportFragmentManager.findFragmentById(android.R.id.content) == null) {
            supportFragmentManager
                    .beginTransaction()
                    .add(android.R.id.content, SettingsFragment())
                    .commit()
        }
    }

    class SettingsFragment: PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            // Load the preferences from an XML resource
            setPreferencesFromResource(R.xml.preferences_main, rootKey)

            val versionName = activity?.packageManager?.getPackageInfo(activity?.packageName, 0)?.versionName
            val versionPreference = findPreference("key_version")
            versionPreference.summary = getString(R.string.summary_version, versionName)
            versionPreference.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                Log.d("SettingsActivity", "Clicked!")
                true
            }
        }
    }
}
