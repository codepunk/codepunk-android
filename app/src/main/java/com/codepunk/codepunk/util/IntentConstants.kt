@file:JvmName("Intents")

package com.codepunk.codepunk.util

import com.codepunk.codepunk.BuildConfig.APPLICATION_ID

private const val ACTION = "$APPLICATION_ID.intent.action"
private const val EXTRA = "$APPLICATION_ID.intent.extra"

const val ACTION_SETTINGS = "$ACTION.SETTINGS"

const val EXTRA_DEVELOPER_PASSWORD_HASH = "$EXTRA.DEVELOPER_PASSWORD_HASH"
const val EXTRA_PREFERENCES_TYPE = "$EXTRA.PREFERENCES_TYPE"