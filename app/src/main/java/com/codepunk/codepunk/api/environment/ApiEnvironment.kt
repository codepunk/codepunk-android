package com.codepunk.codepunk.api.environment

import android.support.annotation.StringRes
import com.codepunk.codepunk.R

enum class ApiEnvironment(@StringRes val nameResId: Int, val baseUrl: String) {
    PROD(
            R.string.api_env_production,
            "" /* TODO */),

    DEV(
            R.string.api_env_development,
            "" /* TODO */),

    LOCAL(
            R.string.api_env_local,
            "https://codepunk.test")
}