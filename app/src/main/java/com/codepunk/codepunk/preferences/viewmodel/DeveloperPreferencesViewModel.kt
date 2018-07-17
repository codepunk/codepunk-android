package com.codepunk.codepunk.preferences.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.SharedPreferences
import com.codepunk.codepunk.BuildConfig
import com.codepunk.codepunk.util.SingleLiveEvent
import com.codepunk.codepunklibstaging.preference.DeveloperModePreference.DeveloperState
import org.jetbrains.anko.defaultSharedPreferences

private const val STEPS_TO_UNLOCK_DEVELOPER_MODE: Int = 7
/*
private const val DEVELOPER_REQUEST_COUNT_MSG: Int = 4
*/

class DeveloperPreferencesViewModel(val app: Application) :
        AndroidViewModel(app),
        SharedPreferences.OnSharedPreferenceChangeListener {

    //region Nested classes

    companion object {
        private val TAG = DeveloperPreferencesViewModel::class.java.simpleName
    }

    //endregion Nested classes

    //region Fields

    var appVersion = MutableLiveData<String>()

    var developerState = MutableLiveData<DeveloperState>()

    var persistedPasswordHash = MutableLiveData<String>()

    var stepsToUnlockDeveloperMode = MutableLiveData<Int>()

    var redundantUnlockRequest = SingleLiveEvent<Void>()

    /*
    var nStepsFromDeveloper = SingleLiveEvent<Int>()

    var redundantUnlockRequest = SingleLiveEvent<Void>()

    private var unlockRequestCount = 0
    */

    //endregion Fields

    //region Constructors

    init {
        appVersion.value = app
                .applicationContext
                .packageManager.getPackageInfo(app.applicationContext.packageName, 0)
                .versionName

        persistedPasswordHash.value =
                app.defaultSharedPreferences.getString(BuildConfig.PREF_KEY_DEV_PASSWORD_HASH, null)

        stepsToUnlockDeveloperMode.value = STEPS_TO_UNLOCK_DEVELOPER_MODE

        // TODO Check if persisted developer hash (if any) is stale. If it is, I guess we need to
        // start the settings activity (if we haven't already)


        app.defaultSharedPreferences.registerOnSharedPreferenceChangeListener(this)

        refreshState()
    }

    //endregion Constructors

    //region Implemented methods
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            BuildConfig.PREF_KEY_DEV_PASSWORD_HASH ->  {
                persistedPasswordHash.value =
                        sharedPreferences?.getString(BuildConfig.PREF_KEY_DEV_PASSWORD_HASH, null)

                refreshState()
            }
        }
    }

    //endregion Implemented methods

    //region Methods

    fun refreshState() {
        developerState.value = when {
            persistedPasswordHash.value == null -> DeveloperState.NOT_DEVELOPER
            BuildConfig.DEVELOPER_PASSWORD_HASH.equals(
                    persistedPasswordHash.value,
                    true) -> DeveloperState.DEVELOPER
            else -> DeveloperState.STALE_PASSWORD
        }
    }

    fun unlockDeveloperMode(): Boolean {
        return stepsToUnlockDeveloperMode.value!!.let { steps ->
            when (steps) {
                0 -> {
                    redundantUnlockRequest.call()
                    false
                }
                else -> {
                    val newValue = steps - 1
                    stepsToUnlockDeveloperMode.value = newValue
                    newValue == 0
                }
            }
        }
    }

    /*
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
    */

    //endregion Methods
}
