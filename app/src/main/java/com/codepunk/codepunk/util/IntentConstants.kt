/*
 * Copyright (C) 2018 Codepunk, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:JvmName("Intents")

package com.codepunk.codepunk.util

import android.content.Intent
import com.codepunk.codepunk.BuildConfig.APPLICATION_ID
import com.codepunk.codepunk.preferences.MainPreferenceFragment

/**
 * Prefix for all "action" [Intent] constants.
 */
private const val ACTION = "$APPLICATION_ID.intent.action"

/**
 * Prefix for all "category" [Intent] constants.
 */
private const val CATEGORY = "$APPLICATION_ID.intent.category"

/**
 * Prefix for all "extra" [Intent] constants.
 */
private const val EXTRA = "$APPLICATION_ID.intent.extra"

/**
 * Activity Action: Show application preferences.
 */
const val ACTION_PREFERENCES = "$ACTION.PREFERENCES"

/**
 * Used with [ACTION_PREFERENCES] to show the main preferences screen.
 */
const val CATEGORY_MAIN = "$CATEGORY.MAIN"

/**
 * Used with [ACTION_PREFERENCES] to show the developer preferences screen.
 */
const val CATEGORY_DEVELOPER = "$CATEGORY.DEVELOPER"

/**
 * Used as an extra field in [MainPreferenceFragment] to get the hash of a successful developer
 * password attempt.
 */
const val EXTRA_DEVELOPER_PASSWORD_HASH = "$EXTRA.DEVELOPER_PASSWORD_HASH"