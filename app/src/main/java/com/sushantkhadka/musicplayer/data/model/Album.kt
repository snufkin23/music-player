package com.sushantkhadka.musicplayer.data.model

import android.net.Uri

/**
 * Represents an album grouping, derived from MediaStore's album table.
 * albumArtUri points to embedded/cached art Coil can load directly.
 */
data class Album(
    val id: Long,
    val name: String,
    val artist: String,
    val trackCount: Int,
    val albumArtUri: Uri?
)
