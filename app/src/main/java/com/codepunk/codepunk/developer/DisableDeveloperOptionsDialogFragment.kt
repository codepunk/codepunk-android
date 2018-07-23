package com.codepunk.codepunk.developer

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatDialogFragment
import com.codepunk.codepunk.R

class DisableDeveloperOptionsDialogFragment: AppCompatDialogFragment(),
        DialogInterface.OnClickListener {

    // region Nested classes

    companion object {
        private val TAG = DeveloperPasswordDialogFragment::class.java.simpleName

        fun newInstance() : DisableDeveloperOptionsDialogFragment {
            return DisableDeveloperOptionsDialogFragment()
        }
    }

    // endregion Nested classes

    // region Inherited methods
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
                .setTitle(R.string.prefs_dev_opts_disable_dialog_title)
                .setMessage(R.string.prefs_dev_opts_disable_dialog_message)
                .setPositiveButton(android.R.string.ok, this)
                .setNegativeButton(android.R.string.cancel, this)
                .create()
    }

    // endregion Inherited methods

    // region Implemented methods

    override fun onClick(dialog: DialogInterface?, which: Int) {
        val resultCode =
                if (which == DialogInterface.BUTTON_POSITIVE) Activity.RESULT_OK
                else Activity.RESULT_CANCELED
        targetFragment?.onActivityResult(targetRequestCode, resultCode, null)
    }

    // endregion Implemented methods
}