package com.codepunk.codepunk.preferences

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.codepunk.codepunk.util.EXTRA_PREFERENCES_TYPE

// TODO Maybe move this to main preferences package. Doesn't really need to be in "view"
// since it's basically a wrapper for preference fragments.

class PreferencesActivity: AppCompatActivity() {

    //region Lifecycle methods

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

    //endregion Lifecycle methods

    //region Inherited methods

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

    //endregion Inherited methods
}