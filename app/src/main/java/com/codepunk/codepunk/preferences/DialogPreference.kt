package com.codepunk.codepunk.preferences

import android.content.Context
import android.support.v7.preference.ListPreference
import android.util.AttributeSet

// TODO I really need my own DialogPreferenceStyle because using the android value makes for smaller text

class DialogPreference @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = android.R.attr.yesNoPreferenceStyle,
        defStyleRes: Int = 0) :
        ListPreference(context, attrs, defStyleAttr, defStyleRes)