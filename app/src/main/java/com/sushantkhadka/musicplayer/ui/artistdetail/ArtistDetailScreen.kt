package com.sushantkhadka.musicplayer.ui.artistdetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.sushantkhadka.musicplayer.R
import com.sushantkhadka.musicplayer.domain.model.Track
import com.sushantkhadka.musicplayer.ui.components.ErrorState
import com.sushantkhadka.musicplayer.ui.components.LoadingState
import com.sushantkhadka.musicplayer.ui.components.TrackRow
import com.sushantkhadka.musicplayer.ui.theme.AppTextStyles
import com.sushantkhadka.musicplayer.ui.theme.Dimens

@Composable
fun ArtistDetailScreen(
    currentMediaId: String?,
    onBack: () -> Unit,
    viewModel: ArtistDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

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
            if (uiState is ArtistDetailUiState.Success) {
                val state = uiState as ArtistDetailUiState.Success
                Text(
                    state.artistName,
                    style = AppTextStyles.headerTitle,
                    modifier = Modifier.padding(start = Dimens.spaceXSmall)
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when (val state = uiState) {
                ArtistDetailUiState.Loading -> LoadingState()
                is ArtistDetailUiState.Error -> ErrorState(state.message)
                is ArtistDetailUiState.Success -> TrackList(
                    tracks = state.tracks,
                    currentMediaId = currentMediaId,
                    onTrackClicked = viewModel::onTrackClicked
                )
            }
        }
    }
}

@Composable
private fun TrackList(
    tracks: List<Track>,
    currentMediaId: String?,
    onTrackClicked: (Track) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(tracks, key = { it.id }) { track ->
            TrackRow(
                title = track.title,
                subtitle = track.album,
                isCurrentlyPlaying = track.id == currentMediaId,
                onClick = { onTrackClicked(track) }
            )
        }
    }
}
