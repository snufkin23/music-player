package com.sushantkhadka.musicplayer.data.model

import android.net.Uri

/**
 * App's own representation of a playable audio track, decoupled from
 * both MediaStore's cursor-based API and ExoPlayer's MediaItem. This
 * is the only Track type ViewModels and UI ever see — mapping to
 * MediaItem happens only at the ExoPlayer boundary, and mapping from
 * MediaStore happens only inside MediaStoreDataSource.
 */
data class Track(
    val id: Long,              // MediaStore _ID — stable reference used across Room tables too
    val title: String,
    val artist: String,
    val album: String,
    val albumId: Long,
    val durationMs: Long,
    val uri: Uri,               // content:// URI, passed to ExoPlayer at playback time
    val filePath: String,
    val sizeBytes: Long,
    val dateAdded: Long,        // epoch seconds, from MediaStore
    val trackNumber: Int? = null,
    val genre: String? = null
)
