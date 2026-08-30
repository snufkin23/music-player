package com.sushantkhadka.musicplayer.playback

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Shared holder for the current ExoPlayer audio session ID.
 *
 * MusicPlaybackService writes to this whenever the session ID changes;
 * anything needing the session ID (system Equalizer/DynamicsProcessing
 * in Phase 2, eventually a custom AudioSink/Oboe path in Phase 3) reads
 * from here via Hilt injection, without needing a direct reference to
 * the service or ExoPlayer instance itself.
 *
 * This is intentionally the only coupling point between playback and
 * any future audio-effects layer.
 */
@Singleton
class PlaybackSessionHolder @Inject constructor() {

    private val _audioSessionId = MutableStateFlow<Int?>(null)
    val audioSessionId: StateFlow<Int?> = _audioSessionId.asStateFlow()

    fun updateSessionId(sessionId: Int) {
        _audioSessionId.value = sessionId
    }

    fun clear() {
        _audioSessionId.value = null
    }
}
