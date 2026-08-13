package com.sushantkhadka.musicplayer.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tracks play count and last-played timestamp per MediaStore track.
 * Powers "recently played" and "most played" library views.
 */
@Entity(tableName = "track_stats")
data class TrackStatsEntity(
    @PrimaryKey
    val mediaStoreTrackId: Long,

    val playCount: Int = 0,

    val lastPlayedAt: Long = System.currentTimeMillis()
)
