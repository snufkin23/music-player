package com.sushantkhadka.musicplayer.ui.library

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

/**
 * Owns track loading and play-initiation only. Playback state
 * (isPlaying, position, current track) now lives in the shared
 * PlayerViewModel — LibraryViewModel just tells PlayerController what
 * to play, it doesn't observe playback state itself.
 */
@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val repository: MusicRepository,
    private val playerController: PlaybackRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LibraryUiState>(LibraryUiState.Loading)
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    private var loadedTracks: List<Track> = emptyList()

    init {
        loadTracks()
        viewModelScope.launch {
            repository.libraryRefreshSignal.collect {
                loadTracks()
            }
        }
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

    fun onTrackClicked(track: Track) {
        val startIndex = loadedTracks.indexOf(track)
        if (startIndex == -1) return

        val mediaItems = loadedTracks.map { it.toMediaItem() }
        playerController.setMediaItems(mediaItems, startIndex, playWhenReady = true)

        viewModelScope.launch {
            repository.recordTrackPlayed(track.id)
        }
    }

    fun refresh() {
        loadTracks()
    }
}
