package com.sushantkhadka.musicplayer.ui.artists

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.sushantkhadka.musicplayer.R
import com.sushantkhadka.musicplayer.domain.model.Artist
import com.sushantkhadka.musicplayer.ui.components.EmptyState
import com.sushantkhadka.musicplayer.ui.components.ErrorState
import com.sushantkhadka.musicplayer.ui.components.LoadingState
import com.sushantkhadka.musicplayer.ui.theme.AppTextStyles
import com.sushantkhadka.musicplayer.ui.theme.Dimens

@Composable
fun ArtistsScreen(
    onArtistClicked: (String) -> Unit,
    viewModel: ArtistsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            ArtistsUiState.Loading -> LoadingState()
            ArtistsUiState.Empty -> EmptyState(stringResource(R.string.artists_empty))
            is ArtistsUiState.Error -> ErrorState(state.message)
            is ArtistsUiState.Success -> ArtistList(
                artists = state.artists,
                onArtistClicked = onArtistClicked
            )
        }
    }
}

@Composable
private fun ArtistList(
    artists: List<Artist>,
    onArtistClicked: (String) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(artists, key = { it.key }) { artist ->
            ArtistRow(artist = artist, onClick = { onArtistClicked(artist.key) })
        }
    }
}

@Composable
private fun ArtistRow(
    artist: Artist,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(horizontal = Dimens.spaceLarge, vertical = Dimens.spaceMedium)) {
            Text(text = artist.name, style = AppTextStyles.itemTitle)
            Text(
                text = pluralStringResource(R.plurals.album_count, artist.albumCount, artist.albumCount) +
                    " • " +
                    pluralStringResource(R.plurals.track_count, artist.trackCount, artist.trackCount),
                style = AppTextStyles.caption,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
