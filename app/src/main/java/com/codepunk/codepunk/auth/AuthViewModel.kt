package com.codepunk.codepunk.auth

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.codepunk.codepunk.BuildConfig
import com.codepunk.codepunk.CodepunkApp.Companion.loginator
import com.codepunk.codepunk.data.api.*
import com.codepunk.codepunk.data.model.User
import com.codepunk.codepunk.data.api.wrapper.DataWrapper
import com.codepunk.codepunk.data.api.wrapper.ErrorWrapper
import com.codepunk.codepunk.data.api.wrapper.ResultWrapper
import com.codepunk.codepunk.data.api.throwable.NullResponseException
import com.codepunk.codepunk.data.api.throwable.UnsuccessfulApiCallException
import com.codepunk.codepunk.data.api.throwable.UserNotAuthenticatedException
import com.codepunk.codepunk.data.model.AuthToken
import com.codepunk.codepunk.util.SharedPreferencesConstants.PREFS_KEY_ACCESS_TOKEN
import com.codepunk.codepunk.util.SharedPreferencesConstants.PREFS_KEY_REFRESH_TOKEN
import com.codepunk.codepunk.util.getApiEnvironment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthViewModel(val app: Application) :
    AndroidViewModel(app), SharedPreferences.OnSharedPreferenceChangeListener {

    // region Properties

    private lateinit var apiEnvironment: ApiEnvironment

    private lateinit var api: ApiPlugin

    private lateinit var authApi: AuthApi

    var userStatus = MutableLiveData<ApiStatus>().apply {
        value = ApiStatus.PENDING
    }

    var userResponse = MutableLiveData<ResultWrapper<User?>>()

    init {
        with(PreferenceManager.getDefaultSharedPreferences(app)) {
            registerOnSharedPreferenceChangeListener(this@AuthViewModel)

            onSharedPreferenceChanged(
                this,
                BuildConfig.PREFS_KEY_API_ENVIRONMENT
            )
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
                authApi = api.retrofit.create(AuthApi::class.java)
            }
        }
    }

    // endregion Implemented methods

    // region Methods

    fun authenticate() {
        val accessToken = PreferenceManager.getDefaultSharedPreferences(app).getString(
            PREFS_KEY_ACCESS_TOKEN,
            null
        )
        if (accessToken == null) {
            // TODO Notify activity
            userResponse.value = ErrorWrapper(UserNotAuthenticatedException())
        } else {
            // Attempt to authenticate
            api.accessToken = accessToken
            userStatus.value = ApiStatus.RUNNING
            api.userApi.getUser().enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>?, response: Response<User>?) {
                    userStatus.value = ApiStatus.FINISHED
                    response?.run {
                        userResponse.value = when {
                            isSuccessful -> DataWrapper(body())
                            else -> ErrorWrapper(UnsuccessfulApiCallException(errorBody()))
                        }
                    } ?: run {
                        userResponse.value = ErrorWrapper(NullResponseException())
                    }
                }

                override fun onFailure(call: Call<User>?, t: Throwable?) {
                    userStatus.value = ApiStatus.FINISHED
                    userResponse.value = ErrorWrapper(t ?: Throwable())
                }
            })
        }
    }

    fun authenticate(email: String, password: String) {
        userStatus.value = ApiStatus.RUNNING
        api.authApi.authToken(
            username = email,
            password = password
        ).enqueue(object : Callback<AuthToken> {
            override fun onResponse(call: Call<AuthToken>?, response: Response<AuthToken>?) {
                userStatus.value = ApiStatus.FINISHED
                response?.run {
                    if (isSuccessful) {
                        body()?.run {
                            PreferenceManager.getDefaultSharedPreferences(app)
                                .edit()
                                .putString(PREFS_KEY_ACCESS_TOKEN, accessToken)
                                .putString(PREFS_KEY_REFRESH_TOKEN, refreshToken)
                                .commit() // TODO commit or listen for change?

                            authenticate()
                        }
                    } else {
                        userResponse.value = ErrorWrapper(UnsuccessfulApiCallException(errorBody()))
                    }
                } ?: run {
                    userResponse.value = ErrorWrapper(NullResponseException())
                }
            }

            override fun onFailure(call: Call<AuthToken>?, t: Throwable?) {
                loginator.d(t.toString(), t ?: Throwable())
                userStatus.value = ApiStatus.FINISHED
                userResponse.value = ErrorWrapper(t ?: Throwable())
            }
        })
    }

    // endregion Methods

}
