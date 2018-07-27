package com.codepunk.codepunk.developer

import android.animation.AnimatorInflater
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
import com.codepunk.codepunklib.view.animation.ShakeInterpolator
import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.codec.digest.MessageDigestAlgorithms

class DeveloperPasswordDialogFragment: AppCompatDialogFragment(),
        DialogInterface.OnShowListener,
        View.OnClickListener {

    // region Nested classes

    companion object {
        private val TAG = DeveloperPasswordDialogFragment::class.java.simpleName

        // TODO Allow for different message
        fun newInstance() : DeveloperPasswordDialogFragment {
            return DeveloperPasswordDialogFragment()
        }
    }

    // endregion Nested classes

    // region Properties

    private val edit by lazy {
        dialog.findViewById(android.R.id.edit) as? EditText
                ?: throw IllegalStateException("Dialog view must contain an EditText with id " +
                        "@android:id/edit")
    }

    private val layout by lazy {
        dialog.findViewById(R.id.layout) as? TextInputLayout
    }

    private val negativeBtn by lazy {
        (dialog as AlertDialog).getButton(AlertDialog.BUTTON_NEGATIVE)
    }

    private val positiveBtn by lazy {
        (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
    }

    private val digestUtils = DigestUtils(MessageDigestAlgorithms.SHA_256)

    private val shakeAnimator by lazy {
        AnimatorInflater.loadAnimator(requireContext(), R.animator.shake).apply {
            interpolator = ShakeInterpolator()
        }
    }

    // endregion Properties

    // region Inherited methods

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
                .setTitle(R.string.prefs_dev_opts_password_dialog_title)
                .setMessage(R.string.prefs_dev_opts_password_dialog_message) // TODO Allow for different message
                .setView(R.layout.fragment_dialog_developer_password)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create().apply {
                    setOnShowListener(this@DeveloperPasswordDialogFragment)
                    shakeAnimator.setTarget(window.decorView)
                }
    }

    // endregion Inherited methods

    // region Implemented methods
    override fun onShow(dialog: DialogInterface?) {
        positiveBtn.setOnClickListener(this)
// TODO        negativeBtn.setOnClickListener(this) <-- Might need more here if stale password
    }

    override fun onClick(view: View?) {
        when (view) {
            positiveBtn -> {
                val password = edit.text.toString()
                val hex = String(Hex.encodeHex(digestUtils.digest(password)))
                if (BuildConfig.DEVELOPER_PASSWORD_HASH.equals(hex, true)) {
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
                    shakeAnimator.start()
                }
            }
            negativeBtn -> {
                if (isCancelable) {
                    dialog.cancel() // TODO Stale?
                }
            }
        }
    }

    // endregion Implemented methods
}