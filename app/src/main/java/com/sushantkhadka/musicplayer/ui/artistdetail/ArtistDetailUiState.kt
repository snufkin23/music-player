package com.sushantkhadka.musicplayer.ui.artistdetail

import com.sushantkhadka.musicplayer.domain.model.Track

sealed interface ArtistDetailUiState {
    data object Loading : ArtistDetailUiState
    data class Success(
        val artistName: String,
        val tracks: List<Track>
    ) : ArtistDetailUiState
    data class Error(val message: String) : ArtistDetailUiState
}
