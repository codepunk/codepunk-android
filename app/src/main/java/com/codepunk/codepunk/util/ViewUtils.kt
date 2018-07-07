package com.codepunk.codepunk.util

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.View
import com.codepunk.codepunk.R

private var shakeAnimator: Animator? = null

fun View.shake(
        distance: Float = context.resources.getDimension(R.dimen.default_shake_distance),
        duration: Long = context.resources.getInteger(android.R.integer.config_mediumAnimTime).toLong(),
        frequency: Int = 5) {

    shakeAnimator?.cancel()
    if (frequency < 1) return

    shakeAnimator = ObjectAnimator.ofFloat(
            this,
            View.TRANSLATION_X,
            0f, distance)
            .setDuration(duration)
            .apply {
                interpolator = ShakeInterpolator(frequency)
            }
    shakeAnimator?.start()
}