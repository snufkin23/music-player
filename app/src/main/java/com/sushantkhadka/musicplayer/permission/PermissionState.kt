package com.sushantkhadka.musicplayer.permission

/**
 * Represents the current state of the app's required runtime permissions,
 * exposed to the UI layer so it can render the correct screen
 * (library, rationale, or "go to settings") without permission logic
 * leaking into Composables.
 */
sealed interface PermissionState {
	data object Granted : PermissionState
	data object NotRequested : PermissionState
	data object ShouldShowRationale : PermissionState
	data object PermanentlyDenied : PermissionState
}