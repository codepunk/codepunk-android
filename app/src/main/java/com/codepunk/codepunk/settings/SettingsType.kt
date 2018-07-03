package com.codepunk.codepunk.settings

import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import com.codepunk.codepunk.R

enum class SettingsType(@StringRes val titleResId: Int) {
    MAIN(R.string.settings_main_activity_title) {
        override fun newFragment(): Fragment {
            return MainSettingsFragment()
        }
    },

    DEVELOPER_OPTIONS(R.string.settings_developer_options_activity_title) {
        override fun newFragment(): Fragment {
            return DeveloperOptionsSettingsFragment()
        }
    };

    abstract fun newFragment(): Fragment
}