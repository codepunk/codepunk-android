package com.codepunk.codepunk.preferences.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.codepunk.codepunk.BuildConfig
import com.codepunk.codepunk.util.SingleLiveEvent
import org.jetbrains.anko.defaultSharedPreferences

private const val DEVELOPER_REQUEST_COUNT_UNLOCK: Int = 7
private const val DEVELOPER_REQUEST_COUNT_MSG: Int = 4

class DeveloperPreferencesViewModel(val app: Application) :
        AndroidViewModel(app) {

    //region Nested classes

    enum class DeveloperState {
        NOT_DEVELOPER,
        AWAITING_PASSWORD,
        STALE_PASSWORD,
        DEVELOPER
    }

    companion object {
        private val TAG = DeveloperPreferencesViewModel::class.java.simpleName
    }

    //endregion Nested classes

    //region Fields

    var appVersion = MutableLiveData<String>()

    var developerState = MutableLiveData<DeveloperState>()

    var nStepsFromDeveloper = SingleLiveEvent<Int>()

    var redundantUnlockRequest = SingleLiveEvent<Void>()

    private var unlockRequestCount = 0

    //endregion Fields

    //region Constructors

    init {
        appVersion.value = app
                .applicationContext
                .packageManager.getPackageInfo(app.applicationContext.packageName, 0)
                .versionName

        refreshState()
    }

    //endregion Constructors

    //region Methods

    fun refreshState() {
        if (app.defaultSharedPreferences.contains(BuildConfig.PREF_KEY_DEV_PASSWORD_HASH)) {
            val hash = app.defaultSharedPreferences.getString(
                    BuildConfig.PREF_KEY_DEV_PASSWORD_HASH, "")
            if (BuildConfig.DEVELOPER_PASSWORD_HASH.equals(hash, true)) {
                developerState.value = DeveloperState.DEVELOPER
            } else {
                developerState.value = DeveloperState.STALE_PASSWORD
            }
        } else {
            developerState.value = DeveloperState.NOT_DEVELOPER
        }
    }

    fun unlockDeveloperOptions() {
        if (developerState == DeveloperState.DEVELOPER) {
            redundantUnlockRequest.call()
        } else {
            unlockRequestCount++
            if (unlockRequestCount >= DEVELOPER_REQUEST_COUNT_UNLOCK) {
                developerState.value = DeveloperState.AWAITING_PASSWORD
            } else if (unlockRequestCount >= DEVELOPER_REQUEST_COUNT_MSG) {
                nStepsFromDeveloper.value = DEVELOPER_REQUEST_COUNT_UNLOCK - unlockRequestCount
            }
        }
    }

    fun registerDeveloper(hash: String?): Boolean {
        return if (BuildConfig.DEVELOPER_PASSWORD_HASH.equals(hash, true)) {
            app.defaultSharedPreferences
                    .edit()
                    .putString(BuildConfig.PREF_KEY_DEV_PASSWORD_HASH, hash)
                    .apply()
            developerState.value = DeveloperState.DEVELOPER
            true
        } else  {
            developerState.value = DeveloperState.NOT_DEVELOPER
            false
        }
    }

    @SuppressLint("ApplySharedPref")
    fun unregisterDeveloper() {
        app.defaultSharedPreferences
                .edit()
                .remove(BuildConfig.PREF_KEY_DEV_PASSWORD_HASH)
                .commit()
        developerState.value = DeveloperState.NOT_DEVELOPER
    }

    //endregion Methods
}
