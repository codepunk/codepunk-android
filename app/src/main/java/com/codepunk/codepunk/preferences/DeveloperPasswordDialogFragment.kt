package com.codepunk.codepunk.preferences

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatDialogFragment
import android.view.View
import android.widget.Button
import com.codepunk.codepunk.BuildConfig
import com.codepunk.codepunk.R
import com.codepunk.codepunk.util.shake
import org.apache.commons.codec.digest.DigestUtils

class DeveloperPasswordDialogFragment: AppCompatDialogFragment(),
        DialogInterface.OnShowListener,
        View.OnClickListener {

    //region Nested classes

    interface OnPasswordResultListener {
        fun onPasswordFailure(dialog: DialogInterface?, password:String)
        fun onPasswordSuccess(dialog: DialogInterface?, password:String, hash: String)
    }

    companion object {
        val FRAGMENT_TAG =
                DeveloperPasswordDialogFragment::class.java.simpleName + ".FRAGMENT_TAG"

        fun newInstance(listener: OnPasswordResultListener): DeveloperPasswordDialogFragment =
                DeveloperPasswordDialogFragment().apply {
                    onPasswordResultListener = listener
                }
    }

    //endregion Nested classes

    //region Fields

    private val passwordLayout: TextInputLayout by lazy {
        dialog.findViewById(R.id.layout_password) as TextInputLayout
    }

    private val passwordEdit: TextInputEditText by lazy {
        dialog.findViewById(R.id.edit_password) as TextInputEditText
    }

    private val okBtn: Button by lazy {
        (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
    }

    var message: CharSequence? = null

    var onPasswordResultListener: OnPasswordResultListener? = null

    var onCancelListener: DialogInterface.OnCancelListener? = null

    //endregion Fields

    //region Inherited methods

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
                .setTitle(R.string.pref_dev_opts_title)
                .setView(R.layout.fragment_dialog_developer_password)
                .setMessage(message ?: getString(R.string.pref_dev_opts_enter_password))
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                    onCancelListener?.onCancel(dialog)
                    dialog.cancel()
                }
                .setCancelable(false)
                .create().also { dialog ->
                    dialog.setOnShowListener(this)
                }
    }

    //endregion Inherited methods

    //region Implemented methods

    override fun /* DialogInterface.OnShowListener */ onShow(dialog: DialogInterface?) {
        okBtn.setOnClickListener(this)
    }

    override fun /* View.OnClickListener */ onClick(view: View?) {
        when (view) {
            okBtn -> {
                val password = passwordEdit.text.toString()
                val hash = DigestUtils.sha256Hex(password)
                if (BuildConfig.DEV_OPTS_PASSWORD_HASH.equals(hash, true)) {
                    dialog.dismiss()
                    onPasswordResultListener?.onPasswordSuccess(dialog, password, hash)
                } else {
                    passwordLayout.error = resources.getString(R.string.pref_dev_opts_incorrect_password)
                    dialog.window.decorView.shake()
                    onPasswordResultListener?.onPasswordFailure(dialog, password)
                }
            }
        }
    }

    //endregion Implemented methods
}
