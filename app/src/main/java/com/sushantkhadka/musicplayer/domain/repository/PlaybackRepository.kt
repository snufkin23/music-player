package com.sushantkhadka.musicplayer.domain.repository

import androidx.media3.common.MediaItem
import com.sushantkhadka.musicplayer.domain.model.PlaybackState
import kotlinx.coroutines.flow.StateFlow

/**
 * Abstraction over the playback engine (Media3 MediaController). The
 * Media3 MediaItem type is intentionally exposed here since Media3 is
 * the playback engine and tracks are handed to it directly.
 */
interface PlaybackRepository {

    val playbackState: StateFlow<PlaybackState>

    val queueItems: StateFlow<List<MediaItem>>

    val currentIndex: StateFlow<Int>

    suspend fun ensureConnected()

    fun setMediaItems(items: List<MediaItem>, startIndex: Int = 0, playWhenReady: Boolean = true)

    fun play()

    fun pause()

    fun seekTo(positionMs: Long)

    fun skipToNext()

    fun skipToPrevious()

    fun getCurrentPosition(): Long

    fun seekToQueueIndex(index: Int)

    fun removeFromQueue(index: Int)

    fun moveQueueItem(fromIndex: Int, toIndex: Int)

    fun toggleShuffle()

    fun cycleRepeatMode()

    fun release()
}
