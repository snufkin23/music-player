package com.sushantkhadka.musicplayer.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

/**
 * Central source of truth for the optional runtime permissions this app
 * requests. Music access no longer uses a broad permission — it is
 * granted per-folder through the Storage Access Framework.
 */
object PermissionUtils {

    /**
     * Notification permission only exists from API 33+. Below that,
     * notifications are granted automatically at install time.
     */
    val notificationPermission: String?
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.POST_NOTIFICATIONS
        } else {
            null
        }

    fun requiredPermissions(): Array<String> {
        return listOfNotNull(notificationPermission).toTypedArray()
    }

    fun isGranted(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun allRequiredPermissionsGranted(context: Context): Boolean {
        return requiredPermissions().all { isGranted(context, it) }
    }
}
