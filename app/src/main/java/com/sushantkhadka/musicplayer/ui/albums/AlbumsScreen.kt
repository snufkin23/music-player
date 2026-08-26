package com.sushantkhadka.musicplayer.ui.albums

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.sushantkhadka.musicplayer.R
import com.sushantkhadka.musicplayer.data.imageloading.AlbumArtRequest
import com.sushantkhadka.musicplayer.domain.model.Album
import com.sushantkhadka.musicplayer.ui.components.EmptyState
import com.sushantkhadka.musicplayer.ui.components.ErrorState
import com.sushantkhadka.musicplayer.ui.components.LoadingState
import com.sushantkhadka.musicplayer.ui.theme.AppTextStyles
import com.sushantkhadka.musicplayer.ui.theme.Dimens

@Composable
fun AlbumsScreen(
    onAlbumClicked: (String) -> Unit,
    viewModel: AlbumsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            AlbumsUiState.Loading -> LoadingState()
            AlbumsUiState.Empty -> EmptyState(stringResource(R.string.albums_empty))
            is AlbumsUiState.Error -> ErrorState(state.message)
            is AlbumsUiState.Success -> AlbumList(
                albums = state.albums,
                onAlbumClicked = onAlbumClicked
            )
        }
    }
}

@Composable
private fun AlbumList(
    albums: List<Album>,
    onAlbumClicked: (String) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(albums, key = { it.key }) { album ->
            AlbumRow(album = album, onClick = { onAlbumClicked(album.key) })
        }
    }
}

@Composable
private fun AlbumRow(
    album: Album,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Dimens.spaceLarge, vertical = Dimens.spaceSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = AlbumArtRequest(album.albumArtUri),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                error = ColorPainter(MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier
                    .size(Dimens.albumArtRow)
                    .clip(RoundedCornerShape(Dimens.cornerRadiusMedium))
            )

            Column(modifier = Modifier.padding(start = Dimens.spaceMedium)) {
                Text(text = album.name, style = AppTextStyles.itemTitle)
                Text(
                    text = "${album.artist} • " +
                        pluralStringResource(R.plurals.track_count, album.trackCount, album.trackCount),
                    style = AppTextStyles.caption,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
