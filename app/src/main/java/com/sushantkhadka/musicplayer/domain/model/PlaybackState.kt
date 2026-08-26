package com.sushantkhadka.musicplayer.domain.model

data class PlaybackState(
    val isConnected: Boolean = false,
    val currentMediaId: String? = null,
    val title: String = "",
    val artist: String = "",
    val isPlaying: Boolean = false,
    val durationMs: Long = 0L,
    val isShuffleEnabled: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF
)
