package com.sushantkhadka.musicplayer.permission

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Owns permission state decisions. The Composable layer only owns the
 * ActivityResult launcher (an Android platform constraint) and reports
 * results back here — all "what state are we in" logic lives in this
 * ViewModel, keeping it unit-testable without Compose/Activity involved.
 */
class PermissionViewModel(application: Application) : AndroidViewModel(application) {

	private val _permissionState = MutableStateFlow(
		if (PermissionUtils.allRequiredPermissionsGranted(application)) {
			PermissionState.Granted
		} else {
			PermissionState.NotRequested
		}
	)
	val permissionState: StateFlow<PermissionState> = _permissionState.asStateFlow()

	/**
	 * Called by the Composable after the system permission dialog result
	 * comes back from the launcher.
	 *
	 * @param allGranted whether every requested permission was granted
	 * @param shouldShowRationaleForAny whether Android indicates we can
	 *   still show a rationale (i.e. not permanently denied) for any
	 *   of the permissions that were denied
	 */
	fun onPermissionResult(allGranted: Boolean, shouldShowRationaleForAny: Boolean) {
		_permissionState.value = when {
			allGranted -> PermissionState.Granted
			shouldShowRationaleForAny -> PermissionState.ShouldShowRationale
			else -> PermissionState.PermanentlyDenied
		}
	}

	/**
	 * Called when the user taps "Grant Permission" or similar — signals
	 * intent to re-trigger the system dialog. The actual launch call
	 * still happens in the Composable, but routing the intent through
	 * the ViewModel keeps a single source of truth for "why are we
	 * launching this."
	 */
	fun requiredPermissions(): Array<String> = PermissionUtils.requiredPermissions()

	/**
	 * Re-check current state, e.g. after returning from system Settings.
	 */
	fun recheckPermissions() {
		val context = getApplication<Application>()
		if (PermissionUtils.allRequiredPermissionsGranted(context)) {
			_permissionState.value = PermissionState.Granted
		}
	}
}