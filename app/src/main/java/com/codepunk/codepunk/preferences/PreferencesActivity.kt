package com.codepunk.codepunk.preferences

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.view.MenuItem
import com.codepunk.codepunk.preferences.view.DeveloperOptionsPreferenceFragment
import com.codepunk.codepunk.preferences.view.MainPreferenceFragment
import com.codepunk.codepunk.util.ACTION_SETTINGS
import com.codepunk.codepunk.util.EXTRA_PREFERENCES_TYPE
import com.codepunk.codepunklib.preference.displayCustomPreferenceDialogFragment

// TODO Maybe move this to main preferences package. Doesn't really need to be in "view"
// since it's basically a wrapper for preference fragments.

class PreferencesActivity: AppCompatActivity(),
        PreferenceFragmentCompat.OnPreferenceDisplayDialogCallback {

    // region Nested classes

    enum class PreferencesType(private val clazz: Class<out PreferenceFragmentCompat>) {

        MAIN(MainPreferenceFragment::class.java),

        DEVELOPER_OPTIONS(DeveloperOptionsPreferenceFragment::class.java);

        fun createFragment(): PreferenceFragmentCompat {
            return clazz.newInstance()
        }
    }

    // endregion Nested classes

    // region Lifecycle methods

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (supportFragmentManager.findFragmentById(android.R.id.content) == null) {
            val preferencesType =
                    (intent.extras?.getSerializable(EXTRA_PREFERENCES_TYPE) as PreferencesType?)
                            ?: PreferencesType.MAIN
            supportFragmentManager
                    .beginTransaction()
                    .add(android.R.id.content, preferencesType.createFragment())
                    .commit()
        }
    }

    // endregion Lifecycle methods

    // region Inherited methods

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                // Always go back instead of "Up"
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // endregion Inherited methods

    // region Implemented methods

    // PreferenceFragmentCompat.OnPreferenceDisplayDialogCallback
    override fun onPreferenceDisplayDialog(
            caller: PreferenceFragmentCompat,
            pref: Preference?): Boolean {
        return caller.displayCustomPreferenceDialogFragment(pref)
    }

    // endregion Implemented methods
    
    // region Methods

    fun startPreferencesActivity(type: PreferencesType) {
        startActivity(Intent(ACTION_SETTINGS).apply {
            putExtras(Bundle().apply {
                putSerializable(EXTRA_PREFERENCES_TYPE, type)
            })
        })
    }
    
    // endregion Methods
}
