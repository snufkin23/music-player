package com.sushantkhadka.musicplayer.ui.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.sushantkhadka.musicplayer.R
import com.sushantkhadka.musicplayer.ui.theme.AppTextStyles
import com.sushantkhadka.musicplayer.ui.theme.Dimens

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val folders by viewModel.folders.collectAsState()
    val folderList = folders.orEmpty()

    val folderLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        if (uri != null) viewModel.addFolder(uri)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spaceSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.action_back))
            }
            Text(stringResource(R.string.settings_title), style = AppTextStyles.headerTitle)
        }

        Text(
            text = stringResource(R.string.settings_folders_title),
            style = AppTextStyles.compactTitle,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(
                horizontal = Dimens.spaceLarge,
                vertical = Dimens.spaceSmall
            )
        )

        if (folderList.isEmpty()) {
            Text(
                text = stringResource(R.string.settings_folders_empty),
                style = AppTextStyles.caption,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = Dimens.spaceLarge)
            )
        } else {
            folderList.forEach { folder ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.spaceLarge, vertical = Dimens.spaceSmall),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = folder.displayName,
                        style = AppTextStyles.itemTitle,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { viewModel.removeFolder(folder) }) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = stringResource(R.string.action_remove_folder)
                        )
                    }
                }
            }
        }

        Button(
            onClick = { folderLauncher.launch(null) },
            modifier = Modifier.padding(
                horizontal = Dimens.spaceLarge,
                vertical = Dimens.spaceMedium
            )
        ) {
            Text(stringResource(R.string.action_add_folder))
        }

        Button(
            onClick = viewModel::rescanLibrary,
            modifier = Modifier.padding(horizontal = Dimens.spaceLarge)
        ) {
            Text(stringResource(R.string.action_rescan_library))
        }
    }
}
