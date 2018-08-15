package com.codepunk.codepunk.auth

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.AppCompatButton
import android.view.View
import com.codepunk.codepunk.R
import com.codepunk.codepunk.util.ACTION_LOGIN

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    private val loginBtn by lazy {
        findViewById<AppCompatButton>(R.id.login_btn)
    }

    private val registerBtn by lazy {
        findViewById<AppCompatButton>(R.id.register_btn)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        loginBtn.setOnClickListener(this)
        registerBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            loginBtn -> {
                finish()
                startActivity(Intent(ACTION_LOGIN))
            }
            registerBtn -> {}
        }
    }
}
