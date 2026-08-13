package com.sushantkhadka.musicplayer.playback

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * Bridges the UI/ViewModel layer to MusicPlaybackService via
 * MediaController. This is the ONLY class ViewModels should depend on
 * for playback — never Media3's MediaController or Player directly.
 * Keeps ViewModels testable with a fake implementation of this class's
 * public surface if needed later.
 *
 * Connection is established lazily on first use and reused as a
 * singleton for the app's lifetime.
 */
@Singleton
class PlayerController @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private var mediaController: MediaController? = null

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

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
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            mediaController?.let { controller ->
                _playbackState.value = _playbackState.value.copy(
                    durationMs = controller.duration.coerceAtLeast(0L)
                )
            }
        }
    }

    /**
     * Ensures the MediaController connection exists, connecting if
     * needed. Safe to call multiple times — subsequent calls are
     * no-ops once connected.
     */
    suspend fun ensureConnected() {
        if (mediaController != null) return

        val sessionToken = SessionToken(
            context,
            ComponentName(context, com.sushantkhadka.musicplayer.playback.MusicPlaybackService::class.java)
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
        _playbackState.value = _playbackState.value.copy(isConnected = true)
    }

    fun setMediaItems(items: List<MediaItem>, startIndex: Int = 0, playWhenReady: Boolean = true) {
        mediaController?.apply {
            setMediaItems(items, startIndex, 0L)
            prepare()
            this.playWhenReady = playWhenReady
        }
    }

    fun play() {
        mediaController?.play()
    }

    fun pause() {
        mediaController?.pause()
    }

    fun seekTo(positionMs: Long) {
        mediaController?.seekTo(positionMs)
    }

    fun skipToNext() {
        mediaController?.seekToNext()
    }

    fun skipToPrevious() {
        mediaController?.seekToPrevious()
    }

    fun getCurrentPosition(): Long {
        return mediaController?.currentPosition ?: 0L
    }

    fun release() {
        mediaController?.removeListener(playerListener)
        mediaController?.release()
        mediaController = null
        _playbackState.value = PlaybackState()
    }
}
