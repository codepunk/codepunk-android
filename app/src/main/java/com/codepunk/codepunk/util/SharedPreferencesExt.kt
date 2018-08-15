package com.codepunk.codepunk.util

import android.content.SharedPreferences
import com.codepunk.codepunk.BuildConfig
import com.codepunk.codepunk.data.api.ApiEnvironment
import com.codepunk.doofenschmirtz.util.enumValueOf

/**
 * Extension function on [SharedPreferences] that gets an [ApiEnvironment] from shared preferences.
 */
fun SharedPreferences.getApiEnvironment(key: String, defaultValue: ApiEnvironment): ApiEnvironment {
    return enumValueOf(getString(key, null), defaultValue)
}

/**
 * Extension function on [SharedPreferences.Editor] that puts an [ApiEnvironment] into the editor.
 */
fun SharedPreferences.Editor.putApiEnvironment(key: String, value: ApiEnvironment) {
    putString(key, value.name)
}

object SharedPreferencesConstants {
    const val PREFS_KEY_ACCESS_TOKEN =
        "${BuildConfig.APPLICATION_ID}.PREFS_KEY_ACCESS_TOKEN"

    const val PREFS_KEY_REFRESH_TOKEN =
        "${BuildConfig.APPLICATION_ID}.PREFS_KEY_REFRESH_TOKEN"
}
