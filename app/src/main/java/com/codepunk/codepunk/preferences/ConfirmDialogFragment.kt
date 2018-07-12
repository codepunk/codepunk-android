package com.codepunk.codepunk.preferences

import android.app.Dialog
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatDialogFragment
import com.codepunk.codepunk.R

class ConfirmDialogFragment: AppCompatDialogFragment() {

    //region Nested classes

    companion object {
        val FRAGMENT_TAG: String = ConfirmDialogFragment::class.java.simpleName

        fun newInstance(): ConfirmDialogFragment {
            return ConfirmDialogFragment()
        }
    }

    //endregion Nested classes

    //region Fields

    var alertDialog: AlertDialog? = dialog as AlertDialog?
    get() = dialog as AlertDialog?

    //endregion Fields

    //region Inherited methods

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
                .setTitle(R.string.dialog_confirm_default_title)
                .setMessage(R.string.dialog_confirm_default_message)
                .setPositiveButton(R.string.dialog_confirm_default_positive_button, null)
                .setNegativeButton(R.string.dialog_confirm_default_negative_button, null)
                .setCancelable(true)
                .create()
    }

    //endregion Inherited methods
}
