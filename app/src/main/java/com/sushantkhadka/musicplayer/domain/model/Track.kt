package com.sushantkhadka.musicplayer.domain.model

import android.net.Uri

data class Track(
    val id: String,
    val uri: Uri,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val trackNumber: Int? = null,
    val albumKey: String,
    val artistKey: String
)
