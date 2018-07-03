package com.codepunk.codepunk.util

import android.support.v7.preference.PreferenceManager

fun PreferenceManager.getKey(key: PreferenceKey): String? {
    return context.getString(key.resId)
}
