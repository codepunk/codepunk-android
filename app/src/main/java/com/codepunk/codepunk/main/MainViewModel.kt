package com.codepunk.codepunk.main

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.widget.Toast
import com.codepunk.codepunk.BuildConfig
import com.codepunk.codepunk.CodepunkApp.Companion.loginator
import com.codepunk.codepunk.data.api.ApiEnvironment
import com.codepunk.codepunk.data.api.ApiPlugin
import com.codepunk.codepunk.data.api.ApiPluginator
import com.codepunk.codepunk.data.api.AuthApi
import com.codepunk.codepunk.data.model.User
import com.codepunk.codepunk.util.getApiEnvironment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(val app: Application) :
    AndroidViewModel(app), SharedPreferences.OnSharedPreferenceChangeListener {

    // region Properties

    private lateinit var apiEnvironment: ApiEnvironment

    private lateinit var api: ApiPlugin

    private lateinit var authApi: AuthApi

    var user = MutableLiveData<User>()

    init {
        with(PreferenceManager.getDefaultSharedPreferences(app)) {
            registerOnSharedPreferenceChangeListener(this@MainViewModel)

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
        with(PreferenceManager.getDefaultSharedPreferences(app)) {
            // val accessToken = getString(PREFS_KEY_ACCESS_TOKEN, null)
            // TODO TEMP
            val accessToken =
                "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6IjNjYjQwOWIxN2NjMDcxYTFhNzk3YmVkYjcyMWY5MWI5MWY1OTE3M2U5ZTBmZWViOWVjMTFjZmUyMzlmMzc2NmU1YWRjZWNlYjFmZmMyODg0In0.eyJhdWQiOiIyIiwianRpIjoiM2NiNDA5YjE3Y2MwNzFhMWE3OTdiZWRiNzIxZjkxYjkxZjU5MTczZTllMGZlZWI5ZWMxMWNmZTIzOWYzNzY2ZTVhZGNlY2ViMWZmYzI4ODQiLCJpYXQiOjE1MzQyNzg4ODEsIm5iZiI6MTUzNDI3ODg4MSwiZXhwIjoxNTY1ODE0ODgxLCJzdWIiOiIxIiwic2NvcGVzIjpbXX0.VEyBAfcCHwKWAE7iuOr9mtphOW6XCwqwwA-7PksQqwHXkIvOmO9wYxdYwMhRXAIBQHsI3moihHoWc8sjheXTW-jEI94ofIUSS6KleqA6XhiR3fL-rzR6yTrbMP3dsb3BdnfDRWFdPG4pXc-1H1XU8mipgi2yxWqFaAnlouiDz2ZJzmjT4rrghSZ2f6CrgKxj2Sco5ZossLbTiz_hYXBMJdMVGoR4X9ARV4boQQ1nemWIK6TwbytPVUX68v5IhQhWl-aCm611vblBYkaD2ESXJH7uc-T7mZj--NGMse8jokK6Lm1vSb1s8NDgQKaLeEG2jfANBfpOUXGjIq7Ru2DpwZ38R9oUnDgcuMSgnh_3CMhEtNSkCIlnNp7AGV-GHdkbrhY1fYuBY1aosxHeFMhb3QzhPUWgmkBhQ3ATd1IuHBJtu85riy1jzKroWTWQBf_kXbGJpQp-0vdh33kApS0tCOi4Z8HN1kAQ5YbOV9P-ezLk1ITh9bhY2LtBoqr5pfLj7P1cLrvZy1DzXwItxFNrRBViVHMN1D90zt_BViVye9jxGaQox-bwT3T6ypUUpRkNJ7FfdDsAObLUFkZVN2ieH0uTsF7xDK0d8QhJFxCMbccCMrm51ThILF-oHoJxQlJ-8ZF15naBGSa1JQxQW7mEF_K9Nc4vDTrJOiMlR59eMvs"
            // END TEMP
            if (accessToken == null) {
                // TODO Notify activity
            } else {
                // Attempt to authenticate
                api.accessToken = accessToken
                val user = api.userApi.getUser()
                user.enqueue(object : Callback<User> {
                    override fun onResponse(call: Call<User>?, response: Response<User>?) {
                        // TODO Check response
                        response?.body()?.run {
                            Toast.makeText(app, "Oh hi, ${this.name}!", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<User>?, t: Throwable?) {
                        loginator.d("Made it! :-(")
                        // TODO Notify activity
                    }
                })
            }
        }
    }

    // endregion Methods

}
