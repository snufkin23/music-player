package com.sushantkhadka.musicplayer.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

/**
 * Central source of truth for which runtime permissions this app needs,
 * gated by API level. Keeping this logic out of Composables keeps it
 * unit-testable and reusable from ViewModels if needed.
 */
object PermissionUtils {

	/**
	 * The permission required to read audio files from the device.
	 * Differs by API level:
	 *  - API 33+ (Tiramisu): READ_MEDIA_AUDIO
	 *  - API 26–32: READ_EXTERNAL_STORAGE
	 */
	val audioPermission: String
		get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			Manifest.permission.READ_MEDIA_AUDIO
		} else {
			Manifest.permission.READ_EXTERNAL_STORAGE
		}

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

	/**
	 * All permissions this app must request at runtime, filtered to
	 * only what's relevant on the current device's API level.
	 */
	fun requiredPermissions(): Array<String> {
		return listOfNotNull(audioPermission, notificationPermission).toTypedArray()
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