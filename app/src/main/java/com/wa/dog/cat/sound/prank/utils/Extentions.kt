package com.wa.dog.cat.sound.prank.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.core.app.ShareCompat
import com.wa.dog.cat.sound.prank.R

fun Context.shareApp(name: String) {
    kotlin.runCatching {
        Intent(Intent.ACTION_SEND).apply {
            type = "text/email"
            putExtra(
                Intent.EXTRA_SUBJECT,
                name + " " + getString(R.string.app_name)
            )
            putExtra(Intent.EXTRA_TEXT, "market://details?id=$packageName")
            startActivity(Intent.createChooser(this, ""))
        }
    }.onFailure { it.printStackTrace() }
}

fun Activity.shareAppLink() {
    kotlin.runCatching {
        ShareCompat.IntentBuilder.from(this)
            .setType("text/plain")
            .setChooserTitle("Chooser title")
            .setText(" " + getString(R.string.app_name) + "\nhttp://play.google.com/store/apps/details?id=$packageName")
            .startChooser()
    }.onFailure { it.printStackTrace() }
}

fun String.setAvatar(): Int {
    return when (this) {
        "en" -> R.drawable.img_english
        "fr" -> R.drawable.img_french
        "es" -> R.drawable.img_spanish
        "in" -> R.drawable.img_indo
        "pt" -> R.drawable.img_portuguese
        "hi" -> R.drawable.img_hindi
        else -> R.drawable.img_vietnam
    }
}
