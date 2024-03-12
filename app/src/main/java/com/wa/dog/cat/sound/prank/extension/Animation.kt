package com.wa.dog.cat.sound.prank.extension

import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator

fun View.animScale(endAction: () -> Unit) {
    animate()
        .scaleX(0.7f)
        .scaleY(0.7f)
        .setDuration(150)
        .setInterpolator(AccelerateDecelerateInterpolator())
        .withEndAction {
            this.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(100)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .withEndAction {
                    endAction()
                }
                .start()
        }
        .start()
}