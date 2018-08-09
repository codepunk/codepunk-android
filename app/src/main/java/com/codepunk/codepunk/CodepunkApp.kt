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

package com.codepunk.codepunk

import android.app.Application
import android.support.v7.preference.PreferenceManager
import com.codepunk.doofenschmirtz.util.loginator.FormattingLoginator
import com.codepunk.doofenschmirtz.util.supportProcessName
import java.lang.Thread.UncaughtExceptionHandler
import java.lang.ref.WeakReference

/**
 * The Application class for the Codepunk app.
 */
class CodepunkApp : Application(), UncaughtExceptionHandler {

    /**
     * The default uncaught exception handler when the app is created.
     */
    private var defaultUncaughtExceptionHandler: WeakReference<UncaughtExceptionHandler?>? = null

    /**
     * Captures the existing default uncaught exception handler and sets defaults from
     * preference XML files.
     */
    override fun onCreate() {
        super.onCreate()
        defaultUncaughtExceptionHandler =
                WeakReference(Thread.getDefaultUncaughtExceptionHandler())
        Thread.setDefaultUncaughtExceptionHandler(this)

        val preferenceResIds = arrayOf(R.xml.preferences_main, R.xml.preferences_developer_options)
        for (resId in preferenceResIds) {
            PreferenceManager.setDefaultValues(this, resId, false)
        }
    }

    // region Implemented methods

    /**
     * Handles uncaught exceptions.
     */
    override fun uncaughtException(t: Thread?, e: Throwable?) {
        loginator.logUncaughtException(t, e, supportProcessName)
        defaultUncaughtExceptionHandler?.get()?.uncaughtException(t, e)
    }

    // endregion Implemented methods

    // region Companion object

    companion object {

        // region Properties

        /**
         * The loginator to use for logging messages.
         */
        val loginator = FormattingLoginator().apply {
            level = BuildConfig.LOGINATOR_LEVEL
        }

        // endregion Properties
    }

    // endregion Companion object

}
