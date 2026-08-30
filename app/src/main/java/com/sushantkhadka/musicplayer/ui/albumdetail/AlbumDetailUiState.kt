package com.sushantkhadka.musicplayer.ui.albumdetail

import android.net.Uri
import com.sushantkhadka.musicplayer.domain.model.Track

sealed interface AlbumDetailUiState {
    data object Loading : AlbumDetailUiState
    data class Success(
        val albumName: String,
        val albumArtist: String,
        val albumArtUri: Uri?,
        val tracks: List<Track>
    ) : AlbumDetailUiState
    data class Error(val message: String) : AlbumDetailUiState
}
