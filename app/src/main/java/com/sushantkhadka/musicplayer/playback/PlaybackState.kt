package com.sushantkhadka.musicplayer.playback

/**
 * UI-facing snapshot of current playback, sourced from MediaController
 * callbacks inside PlayerController. ViewModels observe this directly;
 * no Media3 types (Player, MediaItem) leak past PlayerController.
 */
data class PlaybackState(
    val isConnected: Boolean = false,
    val currentMediaId: String? = null,
    val title: String = "",
    val artist: String = "",
    val isPlaying: Boolean = false,
    val durationMs: Long = 0L
)
