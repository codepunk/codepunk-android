/*
 * Copyright (C) 2018 Codepunk, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codepunk.codepunk.auth

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatImageView
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.codepunk.codepunk.R
import com.codepunk.codepunk.data.model.User
import com.codepunk.codepunk.data.model.UserState
import com.codepunk.codepunk.util.ACTION_PREFERENCES
import com.codepunk.codepunk.util.ACTION_REGISTER
import com.codepunk.codepunk.util.SharedPreferencesConstants
import java.util.*

/**
 * The main [Activity] for the Codepunk app.
 */
class AuthActivity : AppCompatActivity() {

    // region Properties

    /**
     * An instance of [DayPluginator] that displays helpful toasts depending on the day of the
     * week.
     */
    private val dayPluginManager = DayPluginator(this)

    /**
     * The [ViewModel] that stores main/generaldata used by the app.
     */
    private val authViewModel by lazy {
        ViewModelProviders.of(this).get(AuthViewModel::class.java)
    }

    private val loadingImage by lazy {
        findViewById<AppCompatImageView>(R.id.loading_dots_image)
    }

    private val loadingDrawable by lazy {
        loadingImage.drawable as? Animatable
    }

    // endregion Properties

    // region Lifecycle methods

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        dayPluginManager.get(Calendar.getInstance()).showGreeting()

        with(authViewModel) {
            userState.observe(this@AuthActivity, Observer { user ->
                onUserChange(user)
            })

            if (userState.value is UserState.Undefined) {
                val accessToken = PreferenceManager.getDefaultSharedPreferences(app).getString(
                    SharedPreferencesConstants.PREFS_KEY_ACCESS_TOKEN,
                    null
                )
                if (accessToken == null) {
                    startActivity(Intent(ACTION_REGISTER))
                    finish()
                } else {
                    authViewModel.authenticate(accessToken)
                }
            }
        }
    }

    // endregion Lifecycle methods

    // region Inherited methods

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * Handles the various menu options.
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.menu_settings -> {
                startActivity(Intent(ACTION_PREFERENCES))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // endregion Inherited methods

    // region Methods

    private fun onUserChange(user: UserState?) {
        when (user) {
            is User -> {
                loadingDrawable?.stop()
                Toast.makeText(this, "Oh hi, ${user.name}!", Toast.LENGTH_LONG).show()
            }
            is UserState.Loading -> loadingDrawable?.start()
            is UserState.Failure -> {
                loadingDrawable?.stop()
                startActivity(Intent(ACTION_REGISTER))
                finish()
            }
        }
    }

    // endregion Methods
}
