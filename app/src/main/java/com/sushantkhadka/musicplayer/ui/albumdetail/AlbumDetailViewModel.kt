package com.sushantkhadka.musicplayer.ui.albumdetail

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
class AlbumDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: MusicRepository,
    private val playerController: PlaybackRepository
) : ViewModel() {

    private val albumKey: String = checkNotNull(savedStateHandle["albumKey"])

    private val _uiState = MutableStateFlow<AlbumDetailUiState>(AlbumDetailUiState.Loading)
    val uiState: StateFlow<AlbumDetailUiState> = _uiState.asStateFlow()

    private var loadedTracks: List<Track> = emptyList()

    init {
        loadTracks()
    }

    private fun loadTracks() {
        viewModelScope.launch {
            _uiState.value = AlbumDetailUiState.Loading
            try {
                val tracks = repository.getTracksForAlbum(albumKey)
                loadedTracks = tracks
                _uiState.value = AlbumDetailUiState.Success(
                    albumName = tracks.firstOrNull()?.album ?: "Unknown Album",
                    albumArtist = tracks.firstOrNull()?.artist ?: "Unknown Artist",
                    albumArtUri = tracks.firstOrNull()?.uri,
                    tracks = tracks
                )
            } catch (e: Exception) {
                _uiState.value = AlbumDetailUiState.Error(e.message ?: "Failed to load album")
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
