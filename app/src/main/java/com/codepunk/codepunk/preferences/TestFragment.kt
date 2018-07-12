package com.codepunk.codepunk.preferences

import android.os.Bundle
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceDialogFragmentCompat
import android.util.Log

class TestFragment: PreferenceDialogFragmentCompat() {

    companion object {
        val FRAGMENT_TAG = TestFragment::class.java.name + ".FRAGMENT_TAG"

        private val TAG = TestFragment::class.java.simpleName

        fun newInstance(preference: Preference): TestFragment {
            return TestFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_KEY, preference.key)
                }
            }
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        Log.d(TAG, "fragmentTag='" + tag + "', targetFragment=" + (targetFragment?.toString() ?: "NONE"))
    }
}
