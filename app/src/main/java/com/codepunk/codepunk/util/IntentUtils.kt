@file:JvmName("Intents")

package com.codepunk.codepunk.util

import com.codepunk.codepunk.BuildConfig.APPLICATION_ID

private const val BASE = "$APPLICATION_ID.intent"
private const val ACTION = "$BASE.action"
private const val EXTRA = "$BASE.extra"

const val ACTION_SETTINGS = "$ACTION.SETTINGS"

const val EXTRA_PREFERENCES_TYPE = "$EXTRA.PREFERENCES_TYPE"