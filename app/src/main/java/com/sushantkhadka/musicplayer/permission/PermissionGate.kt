package com.sushantkhadka.musicplayer.permission

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Gates access to [content] behind the app's required runtime permissions.
 *
 * This Composable is intentionally thin: it owns only the ActivityResult
 * launcher (an unavoidable platform constraint - launchers must live in
 * Composable/Activity scope) and forwards results to [PermissionViewModel].
 * All state decisions (granted / rationale / permanently denied) live in
 * the ViewModel, per MVVM.
 */
@Composable
fun PermissionGate(
	viewModel: PermissionViewModel = viewModel(),
	content: @Composable () -> Unit
) {
	val context = LocalContext.current
	val activity = context as? Activity
	val permissionState by viewModel.permissionState.collectAsState()

	val launcher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.RequestMultiplePermissions()
	) { results ->
		val allGranted = results.values.all { it }
		val shouldShowRationaleForAny = activity != null && viewModel.requiredPermissions().any {
			activity.shouldShowRequestPermissionRationale(it)
		}
		viewModel.onPermissionResult(allGranted, shouldShowRationaleForAny)
	}

	LaunchedEffect(Unit) {
		if (permissionState == PermissionState.NotRequested) {
			launcher.launch(viewModel.requiredPermissions())
		}
	}

	when (permissionState) {
		PermissionState.Granted -> content()

		PermissionState.NotRequested -> {
			// System dialog is about to take over via LaunchedEffect above.
		}

		PermissionState.ShouldShowRationale -> RationaleScreen(
			onRequestAgain = { launcher.launch(viewModel.requiredPermissions()) }
		)

		PermissionState.PermanentlyDenied -> PermanentlyDeniedScreen(
			onReturnedFromSettings = { viewModel.recheckPermissions() }
		)
	}
}

@Composable
private fun RationaleScreen(onRequestAgain: () -> Unit) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(24.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		Text(
			text = "Musicplayer needs access to your audio files to build " +
					"your library, and to post notifications for playback controls."
		)
		Button(onClick = onRequestAgain) {
			Text("Grant Permission")
		}
	}
}

@Composable
private fun PermanentlyDeniedScreen(onReturnedFromSettings: () -> Unit) {
	val context = LocalContext.current

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(24.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		Text(
			text = "Permission was denied. Please enable audio access " +
					"in Settings to use Musicplayer."
		)
		Button(onClick = {
			val intent = android.content.Intent(
				android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
				android.net.Uri.fromParts("package", context.packageName, null)
			)
			context.startActivity(intent)
			onReturnedFromSettings()
		}) {
			Text("Open Settings")
		}
	}
}