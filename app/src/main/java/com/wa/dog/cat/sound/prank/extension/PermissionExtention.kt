package com.wa.dog.cat.sound.prank.extension

import android.Manifest
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.AppOpsManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Process
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

fun Context.isGrantPermission(permission: String) =
    ContextCompat.checkSelfPermission(
        this, permission
    ) == PackageManager.PERMISSION_GRANTED

@RequiresApi(Build.VERSION_CODES.R)
fun Context.isGrantManageStoragePermission() =
    Environment.isExternalStorageManager()

fun Context.isGrantLocationPermission() =
    isGrantPermission(Manifest.permission.ACCESS_COARSE_LOCATION)

fun Context.isGrantNotificationPermission() =
    if (isAtLeastSdkVersion(Build.VERSION_CODES.TIRAMISU)) {
        isGrantPermission(Manifest.permission.POST_NOTIFICATIONS)
    } else {
        true
    }

fun Context.isGrantReadExternalStoragePermission() =
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        isGrantPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    } else {
        true
    }

fun Context.isGrantPostNotificationPermission() =
    if (isAtLeastSdkVersion(Build.VERSION_CODES.TIRAMISU)) {
        isGrantPermission(Manifest.permission.POST_NOTIFICATIONS)
    } else {
        true
    }

fun Context.isGrantMediaImagePermission() =
    if (isAtLeastSdkVersion(Build.VERSION_CODES.TIRAMISU)) {
        isGrantPermission(Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        isGrantPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

fun Context.isGrantCameraPermission() =
    isGrantPermission(Manifest.permission.CAMERA)

fun Context.isGrantWifiNearByPermission() =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        isGrantPermission(Manifest.permission.NEARBY_WIFI_DEVICES)
    } else {
        true
    }

fun Context.checkSystemWritePermission(): Boolean {
    return Settings.System.canWrite(this)
}

fun Context.openAndroidPermissionsMenu() {
    val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
    intent.data = Uri.parse("package:" + this.packageName)
    startActivity(intent)
}

fun Context.isAccessibilitySettingsOn(nameServiceClass: String): Boolean {
    val am = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    val enabledServices =
        am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
    for (enabledService in enabledServices) {
        if (enabledService == null) {
            continue
        }
        val enabledServiceInfo = enabledService.resolveInfo.serviceInfo
        if (enabledServiceInfo.packageName == packageName && enabledServiceInfo.name == nameServiceClass) return true
    }
    return false
}

fun Context.checkSystemLockOn(): Boolean {
    val cr = contentResolver
    return try {
        val lockPatternEnable: Int =
            Settings.Secure.getInt(cr, Settings.Secure.LOCK_PATTERN_ENABLED)
        lockPatternEnable == 1
    } catch (e: Settings.SettingNotFoundException) {
        false
    }
}

fun Context.checkGPS(): Boolean {
    return (getSystemService(Context.LOCATION_SERVICE) as LocationManager).isProviderEnabled(
        LocationManager.GPS_PROVIDER
    )
}


fun Context.needAutoStartup(): Boolean {
    try {
        val intent = Intent()
        val manufacturer = Build.MANUFACTURER
        if ("xiaomi".equals(manufacturer, ignoreCase = true)) {
            intent.component = ComponentName(
                "com.miui.securitycenter",
                "com.miui.permcenter.autostart.AutoStartManagementActivity"
            )
        } else if ("oppo".equals(manufacturer, ignoreCase = true)) {
            intent.component = ComponentName(
                "com.coloros.safecenter",
                "com.coloros.safecenter.permission.startup.StartupAppListActivity"
            )
        } else if ("vivo".equals(manufacturer, ignoreCase = true)) {
            intent.component = ComponentName(
                "com.vivo.permissionmanager",
                "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
            )
        } else if ("Letv".equals(manufacturer, ignoreCase = true)) {
            intent.component = ComponentName(
                "com.letv.android.letvsafe",
                "com.letv.android.letvsafe.AutobootManageActivity"
            )
        } else if ("Honor".equals(manufacturer, ignoreCase = true)) {
            intent.component = ComponentName(
                "com.huawei.systemmanager",
                "com.huawei.systemmanager.optimize.process.ProtectActivity"
            )
        }
        val list = packageManager.queryIntentActivities(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY
        )
        if (list.size > 0) {
            return true
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return false
}

fun Context.intentAutoStartup() {
    //oppo, vivo, xiomi, letv huawei, and honor
    try {
        val intent = Intent()
        val manufacturer = Build.MANUFACTURER
        if ("xiaomi".equals(manufacturer, ignoreCase = true)) {
            intent.component = ComponentName(
                "com.miui.securitycenter",
                "com.miui.permcenter.autostart.AutoStartManagementActivity"
            )
        } else if ("oppo".equals(manufacturer, ignoreCase = true)) {
            intent.component = ComponentName(
                "com.coloros.safecenter",
                "com.coloros.safecenter.permission.startup.StartupAppListActivity"
            )
        } else if ("vivo".equals(manufacturer, ignoreCase = true)) {
            intent.component = ComponentName(
                "com.vivo.permissionmanager",
                "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
            )
        } else if ("Letv".equals(manufacturer, ignoreCase = true)) {
            intent.component = ComponentName(
                "com.letv.android.letvsafe",
                "com.letv.android.letvsafe.AutobootManageActivity"
            )
        } else if ("Honor".equals(manufacturer, ignoreCase = true)) {
            intent.component = ComponentName(
                "com.huawei.systemmanager",
                "com.huawei.systemmanager.optimize.process.ProtectActivity"
            )
        }
        val list = packageManager.queryIntentActivities(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY
        )
        if (list.size > 0) {
            startActivity(intent)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun isMiUi(): Boolean {
    return getSystemProperty("ro.miui.ui.version.name")?.isNotEmpty() ?: false
}

fun getSystemProperty(key: String?): String? {
    try {
        val props = Class.forName("android.os.SystemProperties")
        return props.getMethod("get", String::class.java).invoke(null, key) as String
    } catch (ignore: Exception) {
    }
    return null
}

fun Context.isPopupPermissionXiaomi(): Boolean {
    return try {
        val appOpsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val cls: Class<*> = appOpsManager.javaClass
        val cls2: Class<*> = Integer.TYPE
        (cls.getMethod("checkOpNoThrow", *arrayOf(cls2, cls2, String::class.java)).invoke(
            appOpsManager, *arrayOf<Any>(
                10021, Integer.valueOf(
                    Process.myUid()
                ), packageName
            )
        ) as Int).toInt() == 0
    } catch (unused: Exception) {
        false
    }
}
