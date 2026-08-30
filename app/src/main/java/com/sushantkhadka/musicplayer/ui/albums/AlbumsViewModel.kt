package com.sushantkhadka.musicplayer.ui.albums

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sushantkhadka.musicplayer.domain.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumsViewModel @Inject constructor(
    private val repository: MusicRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AlbumsUiState>(AlbumsUiState.Loading)
    val uiState: StateFlow<AlbumsUiState> = _uiState.asStateFlow()

    init {
        loadAlbums()
        viewModelScope.launch {
            repository.libraryRefreshSignal.collect {
                loadAlbums()
            }
        }
    }

    private fun loadAlbums() {
        viewModelScope.launch {
            _uiState.value = AlbumsUiState.Loading
            try {
                val albums = repository.getAllAlbums()
                _uiState.value = if (albums.isEmpty()) {
                    AlbumsUiState.Empty
                } else {
                    AlbumsUiState.Success(albums)
                }
            } catch (e: Exception) {
                _uiState.value = AlbumsUiState.Error(e.message ?: "Failed to load albums")
            }
        }
    }
}
