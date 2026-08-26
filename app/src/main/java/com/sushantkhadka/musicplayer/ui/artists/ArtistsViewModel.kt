package com.sushantkhadka.musicplayer.ui.artists

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
class ArtistsViewModel @Inject constructor(
    private val repository: MusicRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ArtistsUiState>(ArtistsUiState.Loading)
    val uiState: StateFlow<ArtistsUiState> = _uiState.asStateFlow()

    init {
        loadArtists()
        viewModelScope.launch {
            repository.libraryRefreshSignal.collect {
                loadArtists()
            }
        }
    }

    private fun loadArtists() {
        viewModelScope.launch {
            _uiState.value = ArtistsUiState.Loading
            try {
                val artists = repository.getAllArtists()
                _uiState.value = if (artists.isEmpty()) {
                    ArtistsUiState.Empty
                } else {
                    ArtistsUiState.Success(artists)
                }
            } catch (e: Exception) {
                _uiState.value = ArtistsUiState.Error(e.message ?: "Failed to load artists")
            }
        }
    }
}
