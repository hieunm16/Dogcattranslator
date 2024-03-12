package com.wa.dog.cat.sound.prank.utils.device

import android.content.Context
import android.net.ConnectivityManager

object DeviceUtils {

    /**
     * Kiểm tra sự có sẵn của quyền root
     */
    fun isRootAvailable(): Boolean {
        System.getenv("PATH")?.split(":")!!.forEach { pathDir ->
            if (java.io.File(pathDir, "su").exists()) {
                return true
            }
        }
        return false
    }

    /**
     * Kiểm tra trạng thái kết nối Wifi
     */
    fun checkWifiConnected(context: Context): Boolean {
        val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        if (mWifi != null) {
            return mWifi.isConnected
        }
        return false
    }

    /**
     * Kiểm tra loại kết nối của thiết bị
     *
     * @return 0: Không kết nối, 1: Wifi, 2: Mobile
     */
    fun getConnectType(context: Context): Int {
        val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)?.isConnectedOrConnecting == true) {
            return 1
        }

        if (connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)?.isConnectedOrConnecting == true) {
            return 2
        }

        return 0
    }

    /**
     * Kiểm tra trạng thái kết nối Internet
     */
    fun checkInternetConnection(ctx: Context): Boolean {
        val conMgr = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        try {
            return conMgr.activeNetworkInfo?.isAvailable == true
                    && conMgr.activeNetworkInfo?.isConnected == true
        } catch (e: NullPointerException) {
            return false
        }
    }
}
