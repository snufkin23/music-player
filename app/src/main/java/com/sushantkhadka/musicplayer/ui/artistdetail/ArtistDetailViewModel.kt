package com.sushantkhadka.musicplayer.ui.artistdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sushantkhadka.musicplayer.domain.model.Track
import com.sushantkhadka.musicplayer.domain.repository.MusicRepository
import com.sushantkhadka.musicplayer.domain.repository.PlaybackRepository
import com.sushantkhadka.musicplayer.playback.toMediaItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: MusicRepository,
    private val playerController: PlaybackRepository
) : ViewModel() {

    private val artistKey: String = checkNotNull(savedStateHandle["artistKey"])

    private val _uiState = MutableStateFlow<ArtistDetailUiState>(ArtistDetailUiState.Loading)
    val uiState: StateFlow<ArtistDetailUiState> = _uiState.asStateFlow()

    private var loadedTracks: List<Track> = emptyList()

    init {
        loadTracks()
    }

    private fun loadTracks() {
        viewModelScope.launch {
            _uiState.value = ArtistDetailUiState.Loading
            try {
                val tracks = repository.getTracksForArtist(artistKey)
                loadedTracks = tracks
                _uiState.value = ArtistDetailUiState.Success(
                    artistName = tracks.firstOrNull()?.artist ?: "Unknown Artist",
                    tracks = tracks
                )
            } catch (e: Exception) {
                _uiState.value = ArtistDetailUiState.Error(e.message ?: "Failed to load artist")
            }
        }
    }

    fun onTrackClicked(track: Track) {
        val startIndex = loadedTracks.indexOf(track)
        if (startIndex == -1) return

        val mediaItems = loadedTracks.map { it.toMediaItem() }
        playerController.setMediaItems(mediaItems, startIndex, playWhenReady = true)

        viewModelScope.launch {
            repository.recordTrackPlayed(track.id)
        }
    }
}
