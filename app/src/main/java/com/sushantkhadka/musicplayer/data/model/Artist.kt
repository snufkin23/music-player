package com.sushantkhadka.musicplayer.data.model

/**
 * Represents an artist grouping, derived from MediaStore's artist table.
 */
data class Artist(
    val id: Long,
    val name: String,
    val albumCount: Int,
    val trackCount: Int
)
