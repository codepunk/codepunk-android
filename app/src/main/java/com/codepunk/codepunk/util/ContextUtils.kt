package com.codepunk.codepunk.util

import android.content.Context
import android.content.Intent

// TODO Move to codepunklib

const val START_LAUNCH_ACTIVITY_DEFAULT_FLAGS =
        Intent.FLAG_ACTIVITY_CLEAR_TOP

fun Context.startLaunchActivity(flags: Int = START_LAUNCH_ACTIVITY_DEFAULT_FLAGS) {
    val intent = packageManager.getLaunchIntentForPackage(packageName)
    intent.flags = flags
    return startActivity(intent)
}