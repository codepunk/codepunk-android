package com.codepunk.codepunk.preferences.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.codepunk.codepunk.BuildConfig

class DeveloperPreferencesViewModel(val app: Application) :
        AndroidViewModel(app),
        SharedPreferences.OnSharedPreferenceChangeListener {

    // region Nested classes

    companion object {
        private val TAG = DeveloperPreferencesViewModel::class.java.simpleName
    }

    // endregion Nested classes

    // region Fields

    var appVersion = MutableLiveData<String>()

    var developerOptionsAuthenticatedHash = MutableLiveData<String>()

    var developerOptionsEnabled = MutableLiveData<Boolean>()

    var developerOptionsUnlocked = MutableLiveData<Boolean>()

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

            developerOptionsEnabled.value =
                    getBoolean(BuildConfig.PREFS_KEY_DEV_OPTS_ENABLED, false)

            developerOptionsUnlocked.value =
                    getBoolean(BuildConfig.PREFS_KEY_DEV_OPTS_UNLOCKED, false)

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

                BuildConfig.PREFS_KEY_DEV_OPTS_ENABLED ->
                    developerOptionsEnabled.value = getBoolean(key, false)

                BuildConfig.PREFS_KEY_DEV_OPTS_UNLOCKED ->
                    developerOptionsUnlocked.value = getBoolean(key, false)
            }
        }
    }

    // endregion Implemented methods

    // region Methods

    fun lockDeveloperOptions() {
        PreferenceManager.getDefaultSharedPreferences(app)
                .edit()
                .putBoolean(BuildConfig.PREFS_KEY_DEV_OPTS_UNLOCKED, false)
                .putBoolean(BuildConfig.PREFS_KEY_DEV_OPTS_ENABLED, false)
                .putString(BuildConfig.PREFS_KEY_DEV_OPTS_AUTHENTICATED_HASH, null)
                .apply()
    }

    fun unlockDeveloperOptions() {
        PreferenceManager.getDefaultSharedPreferences(app)
                .edit()
                .putBoolean(BuildConfig.PREFS_KEY_DEV_OPTS_UNLOCKED, true)
                .putBoolean(BuildConfig.PREFS_KEY_DEV_OPTS_ENABLED, false)
                .putString(BuildConfig.PREFS_KEY_DEV_OPTS_AUTHENTICATED_HASH, null)
                .apply()
    }

    fun enableDeveloperOptions(hash: String) {
        PreferenceManager.getDefaultSharedPreferences(app)
                .edit()
                .putBoolean(BuildConfig.PREFS_KEY_DEV_OPTS_UNLOCKED, true)
                .putBoolean(BuildConfig.PREFS_KEY_DEV_OPTS_ENABLED, true)
                .putString(BuildConfig.PREFS_KEY_DEV_OPTS_AUTHENTICATED_HASH, hash)
                .apply()
    }

    // endregion Methods
}
