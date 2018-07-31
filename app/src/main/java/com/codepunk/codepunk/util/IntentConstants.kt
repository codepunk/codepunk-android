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

import com.codepunk.codepunk.BuildConfig.APPLICATION_ID

private const val ACTION = "$APPLICATION_ID.intent.action"
private const val CATEGORY = "$APPLICATION_ID.intent.category"
private const val EXTRA = "$APPLICATION_ID.intent.extra"

const val ACTION_SETTINGS = "$ACTION.SETTINGS"

const val CATEGORY_MAIN = "$CATEGORY.MAIN"
const val CATEGORY_DEVELOPER = "$CATEGORY.DEVELOPER"

const val EXTRA_DEVELOPER_PASSWORD_HASH = "$EXTRA.DEVELOPER_PASSWORD_HASH"
const val EXTRA_PREFERENCES_TYPE = "$EXTRA.PREFERENCES_TYPE"