package com.sushantkhadka.musicplayer.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Cached scan result for a single audio file. The primary key is the
 * document URI string, which is stable across restarts (persisted
 * Storage Access Framework permission) and used directly for playback.
 */
@Entity(tableName = "tracks")
data class TrackEntity(
    @PrimaryKey
    val id: String,

    val uri: String,

    val title: String,

    val artist: String,

    val album: String,

    val durationMs: Long,

    val trackNumber: Int?,

    val albumKey: String,

    val artistKey: String
)
