package com.codepunk.codepunk.api.environment

import android.support.annotation.StringRes
import com.codepunk.codepunk.R

enum class ApiEnvironment(@StringRes val nameResId: Int, val baseUrl: String) {
    PROD(
            R.string.app_api_environment_name_prod,
            "" /* TODO */),

    DEV(
            R.string.app_api_environment_name_dev,
            "" /* TODO */),

    LOCAL(
            R.string.app_api_environment_name_local,
            "https://codepunk.test")
}