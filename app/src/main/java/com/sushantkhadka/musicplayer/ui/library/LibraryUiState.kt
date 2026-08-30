package com.sushantkhadka.musicplayer.ui.library

import com.sushantkhadka.musicplayer.domain.model.Track

sealed interface LibraryUiState {
    data object Loading : LibraryUiState
    data object Empty : LibraryUiState
    data class Success(val tracks: List<Track>) : LibraryUiState
    data class Error(val message: String) : LibraryUiState
}
