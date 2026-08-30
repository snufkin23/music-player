package com.sushantkhadka.musicplayer.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sushantkhadka.musicplayer.domain.model.Track
import com.sushantkhadka.musicplayer.domain.repository.MusicRepository
import com.sushantkhadka.musicplayer.domain.repository.PlaybackRepository
import com.sushantkhadka.musicplayer.playback.toMediaItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: MusicRepository,
    private val playerController: PlaybackRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var loadedResults: List<Track> = emptyList()

    init {
        viewModelScope.launch {
            _query.debounce(300).collectLatest { currentQuery ->
                if (currentQuery.isBlank()) {
                    loadedResults = emptyList()
                    _uiState.value = SearchUiState.Idle
                    return@collectLatest
                }

                _uiState.value = SearchUiState.Loading
                try {
                    val results = repository.searchTracks(currentQuery)
                    loadedResults = results
                    _uiState.value = if (results.isEmpty()) {
                        SearchUiState.Empty
                    } else {
                        SearchUiState.Success(results)
                    }
                } catch (e: Exception) {
                    _uiState.value = SearchUiState.Error(e.message ?: "Search failed")
                }
            }
        }
    }

    fun onQueryChanged(newQuery: String) {
        _query.value = newQuery
    }

    fun onTrackClicked(track: Track) {
        val startIndex = loadedResults.indexOf(track)
        if (startIndex == -1) return

        val mediaItems = loadedResults.map { it.toMediaItem() }
        playerController.setMediaItems(mediaItems, startIndex, playWhenReady = true)

        viewModelScope.launch {
            repository.recordTrackPlayed(track.id)
        }
    }
}
