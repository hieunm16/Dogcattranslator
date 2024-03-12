package com.wa.dog.cat.sound.prank.extension

import android.app.Activity
import android.content.Context
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat

fun String.containsNumeric() = this.any { it.isDigit() }

fun TextView.setIconDrawableEnd(icon: Int) {
    setCompoundDrawablesWithIntrinsicBounds(
        0,
        0,
        icon,
        0
    )
}

fun Activity.getDrawableWithId(id: Int) =
    ResourcesCompat.getDrawable(this.resources, id, null)

fun Context.getFontWidthId(id: Int) = ResourcesCompat.getFont(this, id)

