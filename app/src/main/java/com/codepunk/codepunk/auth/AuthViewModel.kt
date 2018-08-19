package com.codepunk.codepunk.auth

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.codepunk.codepunk.BuildConfig
import com.codepunk.codepunk.data.api.ApiEnvironment
import com.codepunk.codepunk.data.api.ApiPlugin
import com.codepunk.codepunk.data.api.ApiPluginator
import com.codepunk.codepunk.data.api.AuthWebservice
import com.codepunk.codepunk.data.api.HttpResponseException
import com.codepunk.codepunk.data.model.AuthToken
import com.codepunk.codepunk.data.model.User
import com.codepunk.codepunk.data.model.UserState
import com.codepunk.codepunk.util.SharedPreferencesConstants.PREFS_KEY_ACCESS_TOKEN
import com.codepunk.codepunk.util.SharedPreferencesConstants.PREFS_KEY_REFRESH_TOKEN
import com.codepunk.codepunk.util.getApiEnvironment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthViewModel(
    val app: Application
) : AndroidViewModel(app), SharedPreferences.OnSharedPreferenceChangeListener {

    // region Properties

    private lateinit var apiEnvironment: ApiEnvironment

    private lateinit var api: ApiPlugin

    private lateinit var authWebservice: AuthWebservice

    val user = MutableLiveData<UserState>().apply {
        value = UserState.Pending
    }

    init {
        with(PreferenceManager.getDefaultSharedPreferences(app)) {
            registerOnSharedPreferenceChangeListener(this@AuthViewModel)
            onSharedPreferenceChanged(this, BuildConfig.PREFS_KEY_API_ENVIRONMENT)
        }
    }

    // endregion Properties

    // region Inherited methods

    override fun onCleared() {
        super.onCleared()
        PreferenceManager.getDefaultSharedPreferences(app)
            .unregisterOnSharedPreferenceChangeListener(this)
    }

    // endregion Inherited methods

    // region Implemented methods

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            BuildConfig.PREFS_KEY_API_ENVIRONMENT -> {
                apiEnvironment = sharedPreferences?.getApiEnvironment(
                    key,
                    BuildConfig.DEFAULT_API_ENVIRONMENT
                ) ?: BuildConfig.DEFAULT_API_ENVIRONMENT
                api = ApiPluginator.get(apiEnvironment)
                authWebservice = api.retrofit.create(AuthWebservice::class.java)
            }
        }
    }

    // endregion Implemented methods

    // region Methods

    fun authenticate(accessToken: String) {
        api.accessToken = accessToken
        user.value = UserState.Loading
        api.userWebservice.getUser().enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    user.value = response.body()
                } else {
                    user.value = UserState.Failure(HttpResponseException(response))
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                user.value = UserState.Failure(t)
            }
        })
    }

    fun authenticate(email: String, password: String) {
        user.value = UserState.Loading
        api.authWebservice.authToken(username = email, password = password)
            .enqueue(object : Callback<AuthToken> {
                override fun onResponse(call: Call<AuthToken>, response: Response<AuthToken>) {
                    if (response.isSuccessful) {
                        response.body()?.apply {
                            PreferenceManager.getDefaultSharedPreferences(app)
                                .edit()
                                .putString(PREFS_KEY_ACCESS_TOKEN, accessToken)
                                .putString(PREFS_KEY_REFRESH_TOKEN, refreshToken)
                                .apply()
                            authenticate(accessToken)
                        }
                    } else {
                        user.value = UserState.Failure(HttpResponseException(response))
                    }
                }

                override fun onFailure(call: Call<AuthToken>, t: Throwable) {
                    user.value = UserState.Failure(t)
                }
            })
    }

    // endregion Methods

}
