package com.sushantkhadka.musicplayer.ui.search

import com.sushantkhadka.musicplayer.domain.model.Track

sealed interface SearchUiState {
    data object Idle : SearchUiState
    data object Loading : SearchUiState
    data object Empty : SearchUiState
    data class Success(val tracks: List<Track>) : SearchUiState
    data class Error(val message: String) : SearchUiState
}
