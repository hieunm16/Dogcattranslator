package com.wa.dog.cat.sound.prank.extension

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.Fragment

fun Activity.lockPortraitOrientation() {
    kotlin.runCatching {
        if (isSamsungDeviceBelowAndroid10()) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }.onFailure { it.printStackTrace() }

}

fun isSamsungDeviceBelowAndroid10(): Boolean {
    return try {
        Build.MANUFACTURER.equals(
            "Samsung",
            ignoreCase = true
        ) && !isAtLeastSdkVersion(29)
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }

}

fun Activity.openLocationSettings() = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
    startActivity(this)
}

fun Activity.startSetting() = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
    data = Uri.fromParts("package", packageName, null)
    startActivity(this)
}

fun Activity.setFullScreen() {
    kotlin.runCatching {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }.onFailure { it.printStackTrace() }
}

fun Context.getHeightStatusBar(): Int {
    var statusBarHeight = 0
    kotlin.runCatching {
        val resourceId: Int = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusBarHeight = resources.getDimensionPixelSize(resourceId)
        }
    }.onFailure { it.printStackTrace() }
    if (statusBarHeight == 0) statusBarHeight = 33 * getScreenHeight() / 2400
    return statusBarHeight
}

fun Activity.setStatusBarColor(color: String) {
    kotlin.runCatching {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.parseColor(color)
        window.navigationBarColor = Color.parseColor(color)
    }.onFailure { it.printStackTrace() }
}

fun isAtLeastSdkVersion(versionCode: Int): Boolean {
    return Build.VERSION.SDK_INT >= versionCode
}

fun Activity.shareTextToOtherApp(text: String) {
    kotlin.runCatching {
        Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            startActivity(Intent.createChooser(this, "Share via"))
        }
    }.onFailure { it.printStackTrace() }
}

fun Fragment.shareTextToOtherApp(text: String) {
    kotlin.runCatching {
        Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            startActivity(Intent.createChooser(this, "Share via"))
        }
    }.onFailure { it.printStackTrace() }
}

fun Activity.hideSystemUI() {
    kotlin.runCatching {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                or View.SYSTEM_UI_FLAG_IMMERSIVE)
    }.onFailure { it.printStackTrace() }
}

fun Context.openMarket() {
    try {
        val path = "market://details?id=${packageName}"
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(path)))
    } catch (e: ActivityNotFoundException) {
        kotlin.runCatching {
            val path = "https://play.google.com/store/apps/details?id=${packageName}"
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(path)))
        }.onFailure { it.printStackTrace() }
    }
}

fun Context.sendEmail(mail: String) {
    kotlin.runCatching {
        val uri = Uri.fromParts("mailto", mail, null)
        Intent(Intent.ACTION_SENDTO, uri).apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(mail))
            putExtra(Intent.EXTRA_SUBJECT, "($packageName)")
            putExtra(Intent.EXTRA_TEXT, "")
            startActivity(Intent.createChooser(this, "Send mail"))
        }
    }.onFailure { it.printStackTrace() }
}


fun Context.startEmergencyCall() {
    kotlin.runCatching {
        Intent("com.android.phone.EmergencyDialer.DIAL").apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(this)
        }
    }.onFailure { it.printStackTrace() }
}


fun Context.openCamera() {
    kotlin.runCatching {
        Intent(android.provider.MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA).apply {
            flags = (Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(this)
        }
    }.onFailure { it.printStackTrace() }
}

fun Context.getIconFromPackageName(pka: String): Drawable {
    return try {
        packageManager.getApplicationIcon(pka)
    } catch (e: java.lang.Exception) {
        ColorDrawable(Color.TRANSPARENT)
    }
}

fun Context.openSettingsAccessibility(nameServiceClass: String) {
    kotlin.runCatching {
        Intent("android.settings.ACCESSIBILITY_SETTINGS").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val flattenToString = ComponentName(packageName, nameServiceClass).flattenToString()
            putExtra(":settings:fragment_args_key", flattenToString)
            putExtra(":settings:show_fragment_args", Bundle().apply {
                putString(":settings:fragment_args_key", flattenToString)
            })
            startActivity(this)
        }
    }.onFailure {
        kotlin.runCatching {
            Intent("android.settings.ACCESSIBILITY_SETTINGS").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(this)
            }
        }.onFailure { it.printStackTrace() }
    }
}

