package com.sushantkhadka.musicplayer.ui.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.sushantkhadka.musicplayer.data.model.Track

@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel = hiltViewModel(
		checkNotNull(
			LocalViewModelStoreOwner.current
		) {
			"No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
		}, null
	)
) {
    val uiState by viewModel.uiState.collectAsState()
    val playbackState by viewModel.playbackState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            when (val state = uiState) {
                LibraryUiState.Loading -> LoadingContent()
                LibraryUiState.Empty -> EmptyContent()
                is LibraryUiState.Error -> ErrorContent(state.message)
                is LibraryUiState.Success -> TrackList(
                    tracks = state.tracks,
                    currentMediaId = playbackState.currentMediaId,
                    onTrackClicked = viewModel::onTrackClicked
                )
            }
        }

        MiniPlayerBar(
            title = playbackState.title,
            artist = playbackState.artist,
            isPlaying = playbackState.isPlaying,
            isConnected = playbackState.isConnected,
            onPlayPauseClicked = viewModel::onPlayPauseClicked,
            onSkipNextClicked = viewModel::onSkipNextClicked,
            onSkipPreviousClicked = viewModel::onSkipPreviousClicked
        )
    }
}

@Composable
private fun LoadingContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("No music found on this device.")
    }
}

@Composable
private fun ErrorContent(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Something went wrong: $message")
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
                track = track,
                isCurrentlyPlaying = track.id.toString() == currentMediaId,
                onClick = { onTrackClicked(track) }
            )
        }
    }
}

@Composable
private fun TrackRow(
    track: Track,
    isCurrentlyPlaying: Boolean,
    onClick: () -> Unit
) {
    val background = if (isCurrentlyPlaying) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = background
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(text = track.title, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "${track.artist} • ${track.album}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun MiniPlayerBar(
    title: String,
    artist: String,
    isPlaying: Boolean,
    isConnected: Boolean,
    onPlayPauseClicked: () -> Unit,
    onSkipNextClicked: () -> Unit,
    onSkipPreviousClicked: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title.ifEmpty { if (isConnected) "Nothing playing" else "Connecting…" },
                    style = MaterialTheme.typography.bodyMedium
                )
                if (artist.isNotEmpty()) {
                    Text(text = artist, style = MaterialTheme.typography.bodySmall)
                }
            }

            IconButton(onClick = onSkipPreviousClicked) {
                Icon(Icons.Filled.SkipPrevious, contentDescription = "Previous")
            }
            IconButton(onClick = onPlayPauseClicked) {
                Icon(
                    if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play"
                )
            }
            IconButton(onClick = onSkipNextClicked) {
                Icon(Icons.Filled.SkipNext, contentDescription = "Next")
            }
        }
    }
}
