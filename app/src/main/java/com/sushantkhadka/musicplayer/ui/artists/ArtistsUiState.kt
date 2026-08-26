package com.sushantkhadka.musicplayer.ui.artists

import com.sushantkhadka.musicplayer.domain.model.Artist

sealed interface ArtistsUiState {
    data object Loading : ArtistsUiState
    data object Empty : ArtistsUiState
    data class Success(val artists: List<Artist>) : ArtistsUiState
    data class Error(val message: String) : ArtistsUiState
}
