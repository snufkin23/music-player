package com.sushantkhadka.musicplayer.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sushantkhadka.musicplayer.domain.model.PlaybackState
import com.sushantkhadka.musicplayer.domain.repository.PlaybackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playerController: PlaybackRepository
) : ViewModel() {

    val playbackState: StateFlow<PlaybackState> = playerController.playbackState

    private val _currentPositionMs = MutableStateFlow(0L)
    val currentPositionMs: StateFlow<Long> = _currentPositionMs.asStateFlow()

    val queueItems: StateFlow<List<QueueItemUi>> = playerController.queueItems
        .map { items -> items.map { it.toQueueItemUi() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentQueueIndex: StateFlow<Int> = playerController.currentIndex

    init {
        viewModelScope.launch {
            playerController.ensureConnected()
        }
        startPositionPolling()
    }

    private fun startPositionPolling() {
        viewModelScope.launch {
            while (isActive) {
                if (playbackState.value.isPlaying) {
                    _currentPositionMs.value = playerController.getCurrentPosition()
                }
                delay(200)
            }
        }
    }

    fun onPlayPauseClicked() {
        if (playbackState.value.isPlaying) {
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

    fun onSeek(positionMs: Long) {
        playerController.seekTo(positionMs)
    }

    fun onQueueItemClicked(index: Int) {
        playerController.seekToQueueIndex(index)
    }

    fun onRemoveFromQueue(index: Int) {
        playerController.removeFromQueue(index)
    }

    fun onMoveQueueItem(fromIndex: Int, toIndex: Int) {
        playerController.moveQueueItem(fromIndex, toIndex)
    }

    fun onShuffleClicked() {
        playerController.toggleShuffle()
    }

    fun onRepeatClicked() {
        playerController.cycleRepeatMode()
    }
}
