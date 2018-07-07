package com.codepunk.codepunk

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.codepunk.codepunk.settings.SettingsActivity
import com.codepunk.codepunk.settings.SettingsType
import com.codepunk.codepunk.util.EXTRA_SETTINGS_TYPE
import java.util.*

class MainActivity : AppCompatActivity() {

    private val dayPluginManager: DayPluginManager by lazy {
        DayPluginManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dayPluginManager.get(Calendar.getInstance()).showGreeting()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.menu_settings -> {
                val extras = Bundle()
                extras.putSerializable(EXTRA_SETTINGS_TYPE, SettingsType.MAIN)
                val intent = Intent(this, SettingsActivity::class.java)
                intent.putExtras(extras)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
