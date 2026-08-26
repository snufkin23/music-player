package com.sushantkhadka.musicplayer.ui.setup

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.sushantkhadka.musicplayer.R
import com.sushantkhadka.musicplayer.permission.PermissionUtils
import com.sushantkhadka.musicplayer.ui.settings.SettingsViewModel
import com.sushantkhadka.musicplayer.ui.theme.AppTextStyles
import com.sushantkhadka.musicplayer.ui.theme.Dimens

@Composable
fun FolderGate(
    viewModel: SettingsViewModel = hiltViewModel(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val folders by viewModel.folders.collectAsState()
    val currentFolders = folders

    var contentShown by rememberSaveable { mutableStateOf(false) }

    val folderLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        if (uri != null) viewModel.addFolder(uri)
    }

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { }

    LaunchedEffect(Unit) {
        val permission = PermissionUtils.notificationPermission
        if (permission != null && !PermissionUtils.isGranted(context, permission)) {
            notificationLauncher.launch(arrayOf(permission))
        }
    }

    LaunchedEffect(currentFolders) {
        if (!currentFolders.isNullOrEmpty()) contentShown = true
    }

    if (currentFolders == null) {
        Box(modifier = Modifier.fillMaxSize())
    } else if (currentFolders.isEmpty() && !contentShown) {
        FolderSetupScreen(onChooseFolder = { folderLauncher.launch(null) })
    } else {
        content()
    }
}

@Composable
private fun FolderSetupScreen(onChooseFolder: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.spaceXXLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.folder_setup_title),
            style = AppTextStyles.headerTitle,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(R.string.folder_setup_message),
            style = AppTextStyles.caption,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = Dimens.spaceMedium)
        )
        Button(
            onClick = onChooseFolder,
            modifier = Modifier.padding(top = Dimens.spaceXLarge)
        ) {
            Text(stringResource(R.string.action_choose_folder))
        }
    }
}
