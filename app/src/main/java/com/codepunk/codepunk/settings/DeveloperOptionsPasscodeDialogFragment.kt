package com.codepunk.codepunk.settings

import android.animation.AnimatorSet
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatDialogFragment
import android.support.v7.widget.AppCompatEditText
import android.support.v7.widget.AppCompatTextView
import android.view.View
import com.codepunk.codepunk.BuildConfig
import com.codepunk.codepunk.R
import com.codepunk.codepunk.util.shake
import java.security.MessageDigest

// TODO Need some serious variable/"by lazy" cleaning here

private const val ARG_MESSAGE_RES_ID = "msgResId"

class DeveloperOptionsPasscodeDialogFragment: AppCompatDialogFragment() {

    interface OnPasscodeResultListener: DialogInterface.OnCancelListener {
        fun onPasscodeSuccess(passcode:String, hash: String)
    }

    companion object {
        val FRAGMENT_TAG = DeveloperOptionsPasscodeDialogFragment::class.java.simpleName + ".FRAGMENT_TAG"

        @JvmStatic
        fun newInstance(resId: Int, listener: OnPasscodeResultListener? = null):
                DeveloperOptionsPasscodeDialogFragment =
                DeveloperOptionsPasscodeDialogFragment().apply {
                    onPasscodeResultListener = listener
                    arguments = Bundle().apply {
                        putInt(ARG_MESSAGE_RES_ID, resId)
                    }
                }
    }

    // TODO What happens here with configuration change?
    @StringRes private var messageResId: Int =
            R.string.settings_developer_options_passcode_dialog_message
    set(value) {
        messageView?.setText(value)
        field = value
    }

    private var messageView: AppCompatTextView? = null
    set(value) {
        value?.setText(messageResId)
        field = value
    }

    private var passcodeInput: TextInputEditText? = null

    var onPasscodeResultListener: OnPasscodeResultListener? = null

    var shakeAnimator: AnimatorSet? = null

    //region Lifecycle methods

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            messageResId = it.getInt(ARG_MESSAGE_RES_ID)
        }
    }

    //endregion Lifecycle methods

    //region Inherited methods

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view: View = View.inflate(context, R.layout.fragment_developer_options_passcode_dialog, null)
        messageView = view.findViewById(R.id.txt_message)
        passcodeInput = view.findViewById(R.id.input_passcode)
        val dialog: AlertDialog = AlertDialog.Builder(context!!)
                .setView(view)
                .setTitle(R.string.settings_developer_options_passcode_dialog_title)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel) { _, _ -> dialog.cancel() }
                .setCancelable(true)
                .create()
        dialog.setOnShowListener {
            val button = (it as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {
                checkPasscode(passcodeInput?.text.toString())
            }
        }
        return dialog
    }

    override fun onCancel(dialog: DialogInterface?) {
        //Log.d(DeveloperOptionsPasscodeDialogFragment::class.java.simpleName, "onCancel!!!!!!!!")
        //super.onCancel(dialog)
        onPasscodeResultListener?.onCancel(dialog)
    }



    //endregion Inherited methods

    /*
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?):
            View? {
        return inflater.inflate(
                R.layout.fragment_developer_options_passcode_dialog,
                container,
                false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        messageView = view.findViewById(R.id.txt_message)
        passcodeView.addTextChangedListener(this)
    }
    */

    //region Private methods

    private fun checkPasscode(passcode: String) {
        val hash = hash(passcode)
        if (hash == BuildConfig.DEVELOPER_OPTIONS_PASSCODE_HASH) {
            dialog.dismiss()
            onPasscodeResultListener?.onPasscodeSuccess(passcode, hash)
        } else {
            dialog.window.decorView.shake()
        }
    }

    private fun hash(str: String): String {
        val hash = MessageDigest.getInstance("SHA-256").digest(str.toByteArray())
        val builder = StringBuilder(str.length * 2)
        for (byte in hash) {
            builder.append(String.format("%1$02X", byte))
        }
        return builder.toString()
    }

    //endregion Private methods
}