package com.codepunk.codepunk.preferences.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.codepunk.codepunk.BuildConfig

class DeveloperPreferencesViewModel(val app: Application) :
        AndroidViewModel(app),
        SharedPreferences.OnSharedPreferenceChangeListener {

    // region Nested classes

    enum class DeveloperOptionsState {
        LOCKED,
        UNLOCKED,
        ENABLED
    }

    companion object {
        @Suppress("unused")
        private val TAG = DeveloperPreferencesViewModel::class.java.simpleName
    }

    // endregion Nested classes

    // region Fields

    var appVersion = MutableLiveData<String>()

    private var developerOptionsAuthenticatedHash = MutableLiveData<String>()

    var developerOptionsUnlocked = MutableLiveData<Boolean>()

    var developerOptionsState =
            MediatorLiveData<DeveloperOptionsState>().apply {
                addSource(developerOptionsUnlocked) { unlocked ->
                    updateDeveloperOptionsState(
                            unlocked == true,
                            developerOptionsAuthenticatedHash.value)
                }
                addSource(developerOptionsAuthenticatedHash) { hash ->
                    updateDeveloperOptionsState(
                            developerOptionsUnlocked.value == true,
                            hash)
                }
    }

    // endregion Fields

    // region Constructors

    init {
        appVersion.value = app
                .applicationContext
                .packageManager.getPackageInfo(app.applicationContext.packageName, 0)
                .versionName

        with (PreferenceManager.getDefaultSharedPreferences(app)) {
            this.registerOnSharedPreferenceChangeListener(this@DeveloperPreferencesViewModel)

            developerOptionsAuthenticatedHash.value =
                    getString(BuildConfig.PREFS_KEY_DEV_OPTS_AUTHENTICATED_HASH, null)

            developerOptionsUnlocked.value =
                    getBoolean(BuildConfig.PREFS_KEY_DEV_OPTS_UNLOCKED, false)

            updateDeveloperOptionsState(developerOptionsUnlocked.value == true,
                    developerOptionsAuthenticatedHash.value)

            registerOnSharedPreferenceChangeListener(this@DeveloperPreferencesViewModel)
        }

        // TODO Check if persisted developer hash (if any) is stale. If it is, I guess we need to
        // start the settings activity (if we haven't already)

    }

    // endregion Constructors

    // region Implemented methods

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        sharedPreferences?.apply {
            when (key) {
                BuildConfig.PREFS_KEY_DEV_OPTS_AUTHENTICATED_HASH ->
                    developerOptionsAuthenticatedHash.value = getString(key, null)

                BuildConfig.PREFS_KEY_DEV_OPTS_UNLOCKED ->
                    developerOptionsUnlocked.value = getBoolean(key, false)
            }
        }
    }

    // endregion Implemented methods

    // region Methods

    fun updateDeveloperOptions(unlocked: Boolean, hash: String? = null) {
        val enabled: Boolean = (unlocked && hash != null)
        PreferenceManager.getDefaultSharedPreferences(app)
                .edit()
                .putBoolean(BuildConfig.PREFS_KEY_DEV_OPTS_UNLOCKED, unlocked)
                .putBoolean(BuildConfig.PREFS_KEY_DEV_OPTS_ENABLED, enabled)
                .putString(BuildConfig.PREFS_KEY_DEV_OPTS_AUTHENTICATED_HASH,
                        if (unlocked) hash else null)
                .apply()
    }

    // endregion Methods

    // region Private methods

    private fun updateDeveloperOptionsState(unlocked: Boolean, hash: String? = null) {
        val newValue = when {
            !unlocked -> DeveloperOptionsState.LOCKED
            hash == null -> DeveloperOptionsState.UNLOCKED
            else -> DeveloperOptionsState.ENABLED
        }
        if (developerOptionsState.value != newValue) {
            developerOptionsState.value = newValue
        }
    }

    // endregion Private methods
}
