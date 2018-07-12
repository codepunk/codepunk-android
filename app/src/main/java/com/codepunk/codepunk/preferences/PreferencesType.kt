package com.codepunk.codepunk.preferences

import android.support.v7.preference.PreferenceFragmentCompat
import com.codepunk.codepunk.preferences.view.DeveloperOptionsPreferenceFragment
import com.codepunk.codepunk.preferences.view.MainPreferenceFragment

enum class PreferencesType(private val clazz: Class<out PreferenceFragmentCompat>) {

    MAIN(MainPreferenceFragment::class.java),

    DEVELOPER_OPTIONS(DeveloperOptionsPreferenceFragment::class.java);

    fun createFragment(): PreferenceFragmentCompat {
        return clazz.newInstance()
    }
}