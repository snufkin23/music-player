package com.sushantkhadka.musicplayer.domain.model

import android.net.Uri

/**
 * Represents an album grouping, derived by grouping scanned tracks.
 * albumArtUri points to a representative track whose embedded art can
 * be extracted for display.
 */
data class Album(
    val key: String,
    val name: String,
    val artist: String,
    val trackCount: Int,
    val albumArtUri: Uri?
)
