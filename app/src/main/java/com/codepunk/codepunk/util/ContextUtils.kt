package com.codepunk.codepunk.util

import android.content.Context

// TODO Move to codepunklib

fun Context.startLaunchActivity(flags: Int) {
    val intent = packageManager.getLaunchIntentForPackage(packageName)
    intent.flags = flags
    return startActivity(intent)
}