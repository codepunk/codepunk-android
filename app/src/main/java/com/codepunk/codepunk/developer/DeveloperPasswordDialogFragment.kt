/*
 * Copyright (C) 2018 Codepunk, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codepunk.codepunk.developer

import android.animation.Animator
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
import android.widget.Button
import android.widget.EditText
import com.codepunk.codepunk.BuildConfig
import com.codepunk.codepunk.R
import com.codepunk.codepunk.util.EXTRA_DEVELOPER_PASSWORD_HASH
import com.codepunk.doofenschmirtz.view.animation.ShakeInterpolator
import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.codec.digest.MessageDigestAlgorithms

/**
 * Dialog fragment used to get the developer password from the user. The developer password is not
 * stored anywhere in the app; rather, the SHA-256 hashed password is stored and this class hashes
 * the user's input in order to compare it with the stored hash.
 */
class DeveloperPasswordDialogFragment : AppCompatDialogFragment(),
        DialogInterface.OnShowListener,
        View.OnClickListener {

    // region Properties

    /**
     * The [EditText] in which the user will enter their password. This widget is required and
     * [DeveloperPasswordDialogFragment] will throw an exception if the layout does not contain
     * an EditText with the id @android:id/edit.
     */
    private val edit by lazy {
        dialog.findViewById(android.R.id.edit) as? EditText
                ?: throw IllegalStateException("Dialog view must contain an EditText with id " +
                        "@android:id/edit")
    }

    /**
     * The (optional) [TextInputLayout] that contains the password EditText.
     */
    private val layout by lazy {
        dialog.findViewById(R.id.layout) as? TextInputLayout
    }

    /**
     * The (optional) [Button] that represents a positive response by the user.
     */
    private val positiveBtn by lazy {
        (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
    }

    /**
     * The [DigestUtils] that will be used to create a hash of the user's input.
     */
    private val digestUtils = DigestUtils(MessageDigestAlgorithms.SHA_256)

    /**
     * An [Animator] that will run if the user enters an incorrect password.
     */
    private val shakeAnimator by lazy {
        AnimatorInflater.loadAnimator(requireContext(), R.animator.shake).apply {
            interpolator = ShakeInterpolator()
        }
    }

    // endregion Properties

    // region Inherited methods

    /**
     * Builds the [Dialog] in which the user will enter the developer password.
     */
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

    /**
     * Sets the positive button's OnClickListener when the dialog is shown so we can perform
     * custom logic (i.e. check the password entered by the user)
     */
    // DialogInterface.OnShowListener
    override fun onShow(dialog: DialogInterface?) {
        positiveBtn.setOnClickListener(this)
    }

    /**
     * Tests the password entered by the user against the stored hashed password, and
     * shake the dialog if the user enters the incorrect password.
     */
    // View.OnClickListener
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
        }
    }

    // endregion Implemented methods

    // region Companion object

    companion object {

        // region Methods

        /**
         * Creates a new instance of the [DeveloperPasswordDialogFragment].
         */
        fun newInstance(): DeveloperPasswordDialogFragment {
            return DeveloperPasswordDialogFragment()
        }

        // endregion Methods

    }

    // endregion Companion object
}
