package com.sushantkhadka.musicplayer.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sushantkhadka.musicplayer.data.repository.MusicRepository
import com.sushantkhadka.musicplayer.playback.PlaybackState
import com.sushantkhadka.musicplayer.playback.PlayerController
import com.sushantkhadka.musicplayer.playback.toMediaItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val repository: MusicRepository,
    private val playerController: PlayerController
) : ViewModel() {

    private val _uiState = MutableStateFlow<LibraryUiState>(LibraryUiState.Loading)
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    val playbackState: StateFlow<PlaybackState> = playerController.playbackState

    private var loadedTracks: List<com.sushantkhadka.musicplayer.data.model.Track> = emptyList()

    init {
        viewModelScope.launch {
            playerController.ensureConnected()
        }
        loadTracks()
    }

    private fun loadTracks() {
        viewModelScope.launch {
            _uiState.value = LibraryUiState.Loading
            try {
                val tracks = repository.getAllTracks()
                loadedTracks = tracks
                _uiState.value = if (tracks.isEmpty()) {
                    LibraryUiState.Empty
                } else {
                    LibraryUiState.Success(tracks)
                }
            } catch (e: Exception) {
                _uiState.value = LibraryUiState.Error(e.message ?: "Failed to load library")
            }
        }
    }

    fun onTrackClicked(track: com.sushantkhadka.musicplayer.data.model.Track) {
        val startIndex = loadedTracks.indexOf(track)
        if (startIndex == -1) return

        val mediaItems = loadedTracks.map { it.toMediaItem() }
        playerController.setMediaItems(mediaItems, startIndex, playWhenReady = true)

        viewModelScope.launch {
            repository.recordTrackPlayed(track.id)
        }
    }

    fun onPlayPauseClicked() {
        val isPlaying = playbackState.value.isPlaying
        if (isPlaying) {
            playerController.pause()
        } else {
            playerController.play()
        }
    }

    fun onSkipNextClicked() {
        playerController.skipToNext()
    }

    fun onSkipPreviousClicked() {
        playerController.skipToPrevious()
    }

    fun refresh() {
        loadTracks()
    }
}
