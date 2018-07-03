@file:JvmName("Intents")

package com.codepunk.codepunk.util

import com.codepunk.codepunk.BuildConfig.APPLICATION_ID

private const val BASE = "$APPLICATION_ID.intent"
private const val EXTRA = "$BASE.extra"

const val EXTRA_SETTINGS_TYPE = "$EXTRA.SETTINGS_TYPE"