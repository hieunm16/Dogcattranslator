package com.wa.dog.cat.sound.prank.extension

import android.content.Context
import android.view.View
import androidx.core.view.isVisible
fun Context.getScreenHeight(): Int {
    return resources.displayMetrics.heightPixels
}
fun View.visible() = if (!this.isVisible) this.visibility = View.VISIBLE else {
}

fun View.gone() {
    if (this.isVisible) this.visibility = View.GONE
}

fun View.invisible() = if (this.isVisible) this.visibility = View.INVISIBLE else {
}

fun View.setOnSafeClick(defaultInterval: Int = 600, onSafeClick: (View) -> Unit) {
    val safeClickListener = SafeClickListener(defaultInterval) {
        onSafeClick(it)
    }
    setOnClickListener(safeClickListener)
}

class SafeClickListener(
    private var defaultInterval: Int = 600,
    private val onSafeCLick: (View) -> Unit
) : View.OnClickListener {
    private var lastTimeClicked: Long = 0
    override fun onClick(v: View) {
        if (System.currentTimeMillis() - lastTimeClicked < defaultInterval) {
            return
        }
        lastTimeClicked = System.currentTimeMillis()
        kotlin.runCatching {
            onSafeCLick(v)
        }.onFailure {
            it.printStackTrace()
        }
    }
}