package com.wa.dog.cat.sound.prank.extension

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager

fun Activity.hideKeyboard() {
    if (currentFocus != null) {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            currentFocus!!.windowToken,
            0
        )
    }
}

fun Activity.showSoftKeyboard() {
    (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInput(
        InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY
    )
}

//fun Context.copyText(text: String) = run {
//    val clipboard: ClipboardManager? =
//        getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
//    val clip = ClipData.newPlainText(getString(R.string.copied_text), text)
//    clipboard?.setPrimaryClip(clip)
//}