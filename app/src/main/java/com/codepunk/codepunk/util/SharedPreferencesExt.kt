package com.codepunk.codepunk.util

import android.content.SharedPreferences
import com.codepunk.codepunk.api.environment.ApiEnvironment
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
