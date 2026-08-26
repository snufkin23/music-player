package com.sushantkhadka.musicplayer.ui.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.sushantkhadka.musicplayer.ui.components.EmptyState
import com.sushantkhadka.musicplayer.ui.components.ErrorState
import com.sushantkhadka.musicplayer.ui.components.LoadingState
import com.sushantkhadka.musicplayer.ui.components.TrackRow
import com.sushantkhadka.musicplayer.ui.theme.Dimens

@Composable
fun SearchScreen(
    currentMediaId: String?,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val query by viewModel.query.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = query,
            onValueChange = viewModel::onQueryChanged,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spaceLarge),
            placeholder = { Text(stringResource(R.string.search_placeholder)) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onQueryChanged("") }) {
                        Icon(Icons.Filled.Clear, contentDescription = stringResource(R.string.action_clear))
                    }
                }
            },
            singleLine = true
        )

        Box(modifier = Modifier.fillMaxSize()) {
            when (val state = uiState) {
                SearchUiState.Idle -> IdleContent()
                SearchUiState.Loading -> LoadingState()
                SearchUiState.Empty -> EmptyState(stringResource(R.string.search_no_results, query))
                is SearchUiState.Error -> ErrorState(state.message)
                is SearchUiState.Success -> ResultsList(
                    tracks = state.tracks,
                    currentMediaId = currentMediaId,
                    onTrackClicked = viewModel::onTrackClicked
                )
            }
        }
    }
}

@Composable
private fun IdleContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            stringResource(R.string.search_idle_prompt),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ResultsList(
    tracks: List<Track>,
    currentMediaId: String?,
    onTrackClicked: (Track) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(tracks, key = { it.id }) { track ->
            TrackRow(
                title = track.title,
                subtitle = "${track.artist} • ${track.album}",
                isCurrentlyPlaying = track.id == currentMediaId,
                onClick = { onTrackClicked(track) }
            )
        }
    }
}
