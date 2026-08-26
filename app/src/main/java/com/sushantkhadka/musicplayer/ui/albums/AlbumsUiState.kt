package com.sushantkhadka.musicplayer.ui.albums

import com.sushantkhadka.musicplayer.domain.model.Album

sealed interface AlbumsUiState {
    data object Loading : AlbumsUiState
    data object Empty : AlbumsUiState
    data class Success(val albums: List<Album>) : AlbumsUiState
    data class Error(val message: String) : AlbumsUiState
}
