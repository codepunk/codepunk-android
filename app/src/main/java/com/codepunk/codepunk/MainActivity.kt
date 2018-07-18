package com.codepunk.codepunk

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.codepunk.codepunk.util.ACTION_SETTINGS
import java.util.*

class MainActivity : AppCompatActivity() {

    //region Fields

    private val dayPluginManager = DayPluginManager(this)

    //endregion Fields

    //region Lifecycle methods

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dayPluginManager.get(Calendar.getInstance()).showGreeting()

        // TODO TEMP Always open settings
        startActivity(Intent(ACTION_SETTINGS))
        // END TEMP
    }

    //endregion Lifecycle methods

    //region Inherited methods

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.menu_settings -> {
                startActivity(Intent(ACTION_SETTINGS))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //endregion Inherited methods
}
