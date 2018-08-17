package com.codepunk.codepunk.auth

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatButton
import android.view.View
import android.widget.Toast
import com.codepunk.codepunk.R
import com.codepunk.codepunk.data.model.User
import com.codepunk.codepunk.data.model.UserState
import com.codepunk.codepunk.util.ACTION_REGISTER

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private val emailEdit by lazy {
        findViewById<TextInputEditText>(R.id.email_edit)
    }

    private val passwordEdit by lazy {
        findViewById<TextInputEditText>(R.id.password_edit)
    }

    private val loginBtn by lazy {
        findViewById<AppCompatButton>(R.id.login_btn)
    }

    private val registerBtn by lazy {
        findViewById<AppCompatButton>(R.id.register_btn)
    }

    /**
     * The [ViewModel] that stores main/generaldata used by the app.
     */
    private val authViewModel by lazy {
        ViewModelProviders.of(this).get(AuthViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginBtn.setOnClickListener(this)
        registerBtn.setOnClickListener(this)

        with(authViewModel) {
            userState.observe(this@LoginActivity, Observer { user ->
                onUserChange(user)
            })
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            loginBtn -> {
                // TODO Validate input
                authViewModel.authenticate(
                    emailEdit.text.toString(),
                    passwordEdit.text.toString()
                )
            }
            registerBtn -> {
                finish()
                startActivity(Intent(ACTION_REGISTER))
            }
        }
    }

    // region Methods

    private fun onUserChange(user: UserState?) {
        when (user) {
            is User ->
                Toast.makeText(this,"Oh hi, ${user.name}!", Toast.LENGTH_LONG).show()
            is UserState.Failure ->
                Toast.makeText(this, user.t.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }

    // endregion Methods
}
