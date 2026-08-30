package com.sushantkhadka.musicplayer.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tracks play count and last-played timestamp per track, keyed by its
 * document URI string.
 */
@Entity(tableName = "track_stats")
data class TrackStatsEntity(
    @PrimaryKey
    val trackId: String,

    val playCount: Int = 0,

    val lastPlayedAt: Long = System.currentTimeMillis()
)
