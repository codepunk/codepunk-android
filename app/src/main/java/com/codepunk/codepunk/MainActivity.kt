package com.codepunk.codepunk

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import java.util.*

class MainActivity : AppCompatActivity() {

    private val dayPluginManager: DayPluginManager by lazy {
        DayPluginManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        dayPluginManager.get(Calendar.getInstance()).showGreeting()
    }
}
