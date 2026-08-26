package com.sushantkhadka.musicplayer.data.repository

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.sushantkhadka.musicplayer.domain.model.PlaybackState
import com.sushantkhadka.musicplayer.domain.model.RepeatMode
import com.sushantkhadka.musicplayer.domain.repository.PlaybackRepository
import com.sushantkhadka.musicplayer.playback.MusicPlaybackService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class MediaControllerPlaybackRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : PlaybackRepository {

    private var mediaController: MediaController? = null

    private val _playbackState = MutableStateFlow(PlaybackState())
    override val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private val _queueItems = MutableStateFlow<List<MediaItem>>(emptyList())
    override val queueItems: StateFlow<List<MediaItem>> = _queueItems.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    override val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _playbackState.value = _playbackState.value.copy(isPlaying = isPlaying)
        }

        override fun onMediaMetadataChanged(mediaMetadata: androidx.media3.common.MediaMetadata) {
            _playbackState.value = _playbackState.value.copy(
                title = mediaMetadata.title?.toString() ?: "",
                artist = mediaMetadata.artist?.toString() ?: ""
            )
        }

        override fun onMediaItemTransition(
            mediaItem: MediaItem?,
            reason: Int
        ) {
            _playbackState.value = _playbackState.value.copy(
                currentMediaId = mediaItem?.mediaId
            )
            mediaController?.let { _currentIndex.value = it.currentMediaItemIndex }
        }

        override fun onTimelineChanged(timeline: Timeline, reason: Int) {
            updateQueueFromController()
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            mediaController?.let { controller ->
                _playbackState.value = _playbackState.value.copy(
                    durationMs = controller.duration.coerceAtLeast(0L)
                )
            }
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            _playbackState.value = _playbackState.value.copy(isShuffleEnabled = shuffleModeEnabled)
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            _playbackState.value = _playbackState.value.copy(repeatMode = repeatMode.toRepeatMode())
        }
    }

    private fun updateQueueFromController() {
        val controller = mediaController ?: return
        val items = (0 until controller.mediaItemCount).map { index ->
            controller.getMediaItemAt(index)
        }
        _queueItems.value = items
        _currentIndex.value = controller.currentMediaItemIndex
    }

    override suspend fun ensureConnected() {
        if (mediaController != null) return

        val sessionToken = SessionToken(
            context,
            ComponentName(context, MusicPlaybackService::class.java)
        )

        val controller = suspendCancellableCoroutine<MediaController> { continuation ->
            val future = MediaController.Builder(context, sessionToken).buildAsync()
            future.addListener(
                {
                    if (continuation.isActive) {
                        continuation.resume(future.get())
                    }
                },
                { runnable -> runnable.run() }
            )
        }

        controller.addListener(playerListener)
        mediaController = controller
        _playbackState.value = _playbackState.value.copy(
            isConnected = true,
            isShuffleEnabled = controller.shuffleModeEnabled,
            repeatMode = controller.repeatMode.toRepeatMode()
        )
    }

    override fun setMediaItems(items: List<MediaItem>, startIndex: Int, playWhenReady: Boolean) {
        mediaController?.apply {
            setMediaItems(items, startIndex, 0L)
            prepare()
            this.playWhenReady = playWhenReady
        }
    }

    override fun play() {
        mediaController?.play()
    }

    override fun pause() {
        mediaController?.pause()
    }

    override fun seekTo(positionMs: Long) {
        mediaController?.seekTo(positionMs)
    }

    override fun skipToNext() {
        mediaController?.seekToNext()
    }

    override fun skipToPrevious() {
        mediaController?.seekToPrevious()
    }

    override fun getCurrentPosition(): Long {
        return mediaController?.currentPosition ?: 0L
    }

    override fun seekToQueueIndex(index: Int) {
        mediaController?.apply {
            seekTo(index, 0L)
            play()
        }
    }

    override fun removeFromQueue(index: Int) {
        mediaController?.removeMediaItem(index)
    }

    override fun moveQueueItem(fromIndex: Int, toIndex: Int) {
        mediaController?.moveMediaItem(fromIndex, toIndex)
    }

    override fun toggleShuffle() {
        mediaController?.let { it.shuffleModeEnabled = !it.shuffleModeEnabled }
    }

    override fun cycleRepeatMode() {
        mediaController?.let { controller ->
            val next = when (controller.repeatMode.toRepeatMode()) {
                RepeatMode.OFF -> RepeatMode.ALL
                RepeatMode.ALL -> RepeatMode.ONE
                RepeatMode.ONE -> RepeatMode.OFF
            }
            controller.repeatMode = next.toPlayerRepeatMode()
        }
    }

    private fun Int.toRepeatMode(): RepeatMode = when (this) {
        Player.REPEAT_MODE_ONE -> RepeatMode.ONE
        Player.REPEAT_MODE_ALL -> RepeatMode.ALL
        else -> RepeatMode.OFF
    }

    private fun RepeatMode.toPlayerRepeatMode(): Int = when (this) {
        RepeatMode.ONE -> Player.REPEAT_MODE_ONE
        RepeatMode.ALL -> Player.REPEAT_MODE_ALL
        RepeatMode.OFF -> Player.REPEAT_MODE_OFF
    }

    override fun release() {
        mediaController?.removeListener(playerListener)
        mediaController?.release()
        mediaController = null
        _playbackState.value = PlaybackState()
        _queueItems.value = emptyList()
    }
}
