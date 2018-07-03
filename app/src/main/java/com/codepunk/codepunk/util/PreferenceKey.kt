package com.codepunk.codepunk.util

import android.support.annotation.StringRes
import com.codepunk.codepunk.R

enum class PreferenceKey(@StringRes val resId: Int) {
    API_ENVIRONMENT(R.string.preference_key_api_environment),
    DEVELOPER(R.string.preference_key_developer),
    DEVELOPER_OPTIONS(R.string.preference_key_developer_options),
    VERSION(R.string.preference_key_version);
}