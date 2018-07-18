package com.codepunk.codepunk.preferences.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.codepunk.codepunk.BuildConfig
import com.codepunk.codepunk.util.SingleLiveEvent
import com.codepunk.codepunk.util.enumValueOf

private const val STEPS_TO_UNLOCK_DEVELOPER_MODE: Int = 7

class DeveloperPreferencesViewModel(val app: Application) :
        AndroidViewModel(app),
        SharedPreferences.OnSharedPreferenceChangeListener {

    //region Nested classes

    enum class DeveloperOptionsState {
        LOCKED,
        UNLOCKED;

        companion object {
            fun valueFrom(sharedPreferences: SharedPreferences?): DeveloperOptionsState {
                return sharedPreferences?.run {
                    enumValueOf(getString(BuildConfig.PREFS_KEY_DEV_OPTS, ""), LOCKED)
                } ?: LOCKED
            }
        }
    }

    companion object {
        private val TAG = DeveloperPreferencesViewModel::class.java.simpleName
    }

    //endregion Nested classes

    //region Fields

    var appVersion = MutableLiveData<String>()

    var developerOptionsState =  MediatorLiveData<DeveloperOptionsState>()

    var redundantUnlockRequest = SingleLiveEvent<Void>()

    var stepsToUnlockDeveloperMode = MutableLiveData<Int>()

    //endregion Fields

    //region Constructors

    init {
        appVersion.value = app
                .applicationContext
                .packageManager.getPackageInfo(app.applicationContext.packageName, 0)
                .versionName

        with (PreferenceManager.getDefaultSharedPreferences(app)) {
            developerOptionsState.value = DeveloperOptionsState.valueFrom(this)
            developerOptionsState.addSource(stepsToUnlockDeveloperMode) { steps ->
                val state = if (steps?.compareTo(0) == 1) DeveloperOptionsState.LOCKED
                else DeveloperOptionsState.UNLOCKED
                edit().putString(BuildConfig.PREFS_KEY_DEV_OPTS, state.name).apply()
            }

            stepsToUnlockDeveloperMode.value =
                    when (developerOptionsState.value) {
                        DeveloperOptionsState.UNLOCKED -> 0
                        else -> STEPS_TO_UNLOCK_DEVELOPER_MODE
                    }

            registerOnSharedPreferenceChangeListener(this@DeveloperPreferencesViewModel)
        }

        // TODO Check if persisted developer hash (if any) is stale. If it is, I guess we need to
        // start the settings activity (if we haven't already)

    }

    //endregion Constructors

    //region Implemented methods
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            BuildConfig.PREFS_KEY_DEV_OPTS -> {
                developerOptionsState.value = DeveloperOptionsState.valueFrom(sharedPreferences)
            }
        }
    }

    //endregion Implemented methods

    //region Methods

    fun requestUnlockDeveloperMode() {
        val steps = stepsToUnlockDeveloperMode.value ?: 0
        if (steps > 0) {
            stepsToUnlockDeveloperMode.value = steps - 1
        } else {
            redundantUnlockRequest.call()
        }
    }

    //endregion Methods
}
