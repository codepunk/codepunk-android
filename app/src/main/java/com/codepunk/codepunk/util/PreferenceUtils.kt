package com.codepunk.codepunk.util

import android.support.v7.preference.ListPreference

// TODO Move to codepunklib

fun <E : Enum<E>> ListPreference.populate(
        enumClass: Class<E>,
        entryValue: (enum: E) -> CharSequence? = { it.name },
        entry: (enum: E) -> CharSequence? = { it.name }) {
    val entryValueList: ArrayList<CharSequence> = ArrayList(enumClass.enumConstants.size)
    val entryList: ArrayList<CharSequence> = ArrayList(enumClass.enumConstants.size)
    for (constant in enumClass.enumConstants) {
        entryValueList.add(entryValue(constant) ?: constant.name)
        entryList.add(entry(constant) ?: constant.name)
    }
    entryValues = entryValueList.toTypedArray()
    entries = entryList.toTypedArray()
}