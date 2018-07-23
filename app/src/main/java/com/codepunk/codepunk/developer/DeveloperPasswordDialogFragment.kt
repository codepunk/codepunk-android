package com.codepunk.codepunk.developer

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatDialogFragment
import android.view.View
import android.widget.EditText
import com.codepunk.codepunk.BuildConfig
import com.codepunk.codepunk.R
import com.codepunk.codepunk.util.EXTRA_DEVELOPER_PASSWORD_HASH
import com.codepunk.codepunklibstaging.util.shake
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.codec.digest.MessageDigestAlgorithms
import org.jetbrains.anko.findOptional

class DeveloperPasswordDialogFragment: AppCompatDialogFragment(),
        DialogInterface.OnShowListener,
        View.OnClickListener {

    // region Nested classes

    interface OnPrepareDialogBuilderListener {
        fun onPrepareDialogBuilder(builder: AlertDialog.Builder)
    }

    companion object {
        private val TAG = DeveloperPasswordDialogFragment::class.java.simpleName

        fun newInstance(listener: OnPrepareDialogBuilderListener? = null) :
                DeveloperPasswordDialogFragment {
            return DeveloperPasswordDialogFragment().apply {
                onPrepareDialogBuilderListener = listener
            }
        }
    }

    // endregion Nested classes

    // region Properties

    private val edit by lazy {
        dialog.findViewById<EditText>(android.R.id.edit)
                ?: throw IllegalStateException("Dialog view must contain an EditText with id " +
                        "@android:id/edit")
    }

    private val layout by lazy {
        dialog.findOptional<TextInputLayout>(R.id.layout)
    }

    private val negativeBtn by lazy {
        (dialog as AlertDialog).getButton(AlertDialog.BUTTON_NEGATIVE)
    }

    private val positiveBtn by lazy {
        (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
    }

    private val digestUtils = DigestUtils(MessageDigestAlgorithms.SHA_256)

    var onPrepareDialogBuilderListener: OnPrepareDialogBuilderListener? = null

    // endregion Properties

    // region Inherited methods

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
                .setTitle(R.string.prefs_dev_opts_password_dialog_title)
                .setMessage(R.string.prefs_dev_opts_password_dialog_message)
                .setView(R.layout.fragment_dialog_developer_password)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .apply {
                    onPrepareDialogBuilderListener?.onPrepareDialogBuilder(this)
                }
                .create().apply {
                    setOnShowListener(this@DeveloperPasswordDialogFragment)
                }
    }

    // endregion Inherited methods

    // region Implemented methods
    override fun onShow(dialog: DialogInterface?) {
        positiveBtn.setOnClickListener(this)
// TODO        negativeBtn.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view) {
            positiveBtn -> {
                val password = edit.text.toString()
                if (BuildConfig.DEVELOPER_PASSWORD_HASH.equals(
                                digestUtils.digestAsHex(password), true)) {
                    dialog.dismiss()
                    targetFragment?.onActivityResult(
                            targetRequestCode,
                            Activity.RESULT_OK,
                            Intent().apply {
                                putExtra(
                                        EXTRA_DEVELOPER_PASSWORD_HASH,
                                        BuildConfig.DEVELOPER_PASSWORD_HASH)
                            })
                } else {
                    layout?.error = getString(R.string.incorrect_password)
                    dialog.window.decorView.shake()
                }
            }
            negativeBtn -> {
                if (isCancelable) {
                    dialog.cancel()
                }
            }
        }
    }

    // endregion Implemented methods
}