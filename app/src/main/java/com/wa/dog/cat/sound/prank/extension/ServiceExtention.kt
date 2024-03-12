package com.wa.dog.cat.sound.prank.extension

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build

fun Intent.startService(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        context.startForegroundService(this)
    } else {
        context.startService(this)
    }
}

fun Context.stopServiceWithClass(serviceClass: Class<*>) {
    if (isMyServiceRunning(serviceClass)) {
        val serviceIntent = Intent(this, serviceClass)
        stopService(serviceIntent)
    }
}

fun Context.isMyServiceRunning(serviceClass: Class<*>): Boolean {
    val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    for (service in manager.getRunningServices(Int.MAX_VALUE)) {
        if (serviceClass.name == service.service.className) return true
    }
    return false
}