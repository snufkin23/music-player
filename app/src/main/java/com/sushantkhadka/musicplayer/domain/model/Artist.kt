package com.sushantkhadka.musicplayer.domain.model

/**
 * Represents an artist grouping, derived by grouping scanned tracks.
 */
data class Artist(
    val key: String,
    val name: String,
    val albumCount: Int,
    val trackCount: Int
)
