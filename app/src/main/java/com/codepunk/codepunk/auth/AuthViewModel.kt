package com.codepunk.codepunk.auth

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.codepunk.codepunk.BuildConfig
import com.codepunk.codepunk.data.api.*
import com.codepunk.codepunk.data.api.throwable.LaravelErrorException
import com.codepunk.codepunk.data.api.wrapper.DataWrapper
import com.codepunk.codepunk.data.api.wrapper.ErrorWrapper
import com.codepunk.codepunk.data.api.wrapper.ResultWrapper
import com.codepunk.codepunk.data.model.AuthToken
import com.codepunk.codepunk.data.model.LaravelError
import com.codepunk.codepunk.data.model.User
import com.codepunk.codepunk.util.SharedPreferencesConstants.PREFS_KEY_ACCESS_TOKEN
import com.codepunk.codepunk.util.SharedPreferencesConstants.PREFS_KEY_REFRESH_TOKEN
import com.codepunk.codepunk.util.getApiEnvironment
import retrofit2.Call
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

    var userResult = MutableLiveData<ResultWrapper<User?>>()

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

    fun authenticate(accessToken: String) {
        api.accessToken = accessToken
        userStatus.value = ApiStatus.RUNNING
        api.userApi.getUser().enqueue(object : BaseCallback<User, LaravelError>(
            { string -> api.laravelErrorFromJson(string) }
        ) {
            override fun onSuccess(call: Call<User>, response: Response<User>?, body: User?) {
                userStatus.value = ApiStatus.FINISHED
                userResult.value = DataWrapper(body)
            }

            override fun onFailure(
                call: Call<User>,
                response: Response<User>?,
                error: LaravelError?
            ) {
                userStatus.value = ApiStatus.FINISHED
                userResult.value = ErrorWrapper(LaravelErrorException(error))
            }

            override fun onFailure(call: Call<User>, t: Throwable?) {
                userStatus.value = ApiStatus.FINISHED
                userResult.value = ErrorWrapper(t ?: Throwable())
            }
        })
    }

    fun authenticate(email: String, password: String) {
        userStatus.value = ApiStatus.RUNNING
        api.authApi.authToken(
            username = email,
            password = password
        ).enqueue(object : BaseCallback<AuthToken, LaravelError>(
            { string -> api.laravelErrorFromJson(string) }
        ) {
            override fun onSuccess(
                call: Call<AuthToken>,
                response: Response<AuthToken>?,
                body: AuthToken?
            ) {
                userStatus.value = ApiStatus.FINISHED
                body?.run {
                    PreferenceManager.getDefaultSharedPreferences(app)
                        .edit()
                        .putString(PREFS_KEY_ACCESS_TOKEN, accessToken)
                        .putString(PREFS_KEY_REFRESH_TOKEN, refreshToken)
                        .apply()
                    authenticate(accessToken)
                }
            }

            override fun onFailure(
                call: Call<AuthToken>,
                response: Response<AuthToken>?,
                error: LaravelError?
            ) {
                userStatus.value = ApiStatus.FINISHED
                userResult.value = ErrorWrapper(LaravelErrorException(error))
            }

            override fun onFailure(call: Call<AuthToken>, t: Throwable?) {
                userStatus.value = ApiStatus.FINISHED
                userResult.value = ErrorWrapper(t ?: Throwable())
            }
        })
    }

    // endregion Methods

}
