package com.codepunk.codepunk.util

import android.content.Context

fun Context.startLaunchActivity(flags: Int): Unit {
    val intent = packageManager.getLaunchIntentForPackage(packageName)
    intent.flags = flags
    return startActivity(intent)
}