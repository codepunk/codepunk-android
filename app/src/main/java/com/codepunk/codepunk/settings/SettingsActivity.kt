package com.codepunk.codepunk.settings

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.codepunk.codepunk.util.EXTRA_SETTINGS_TYPE
import org.jetbrains.anko.AnkoLogger

class SettingsActivity : AppCompatActivity(), AnkoLogger {

    //region Lifecycle methods

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val settingsType: SettingsType = intent.extras?.getSerializable(EXTRA_SETTINGS_TYPE) as SettingsType
        setTitle(settingsType.titleResId)

        if (supportFragmentManager.findFragmentById(android.R.id.content) == null) {
            supportFragmentManager
                    .beginTransaction()
                    .add(android.R.id.content, settingsType.newFragment())
                    .commit()
        }
    }

    //endregion Lifecycle methods

    //region Inherited methods

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //endregion Inherited methods
}
