package com.wa.dog.cat.sound.prank.extension

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager

fun isNetworkAvailable(): Boolean {
    return Runtime.getRuntime().exec("ping -c 1 google.com").waitFor() == 0
}


fun Context.checkInternetConnection(): Boolean {
    try {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return false
}
