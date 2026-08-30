package com.sushantkhadka.musicplayer.data.local.database.entity

import androidx.room.Entity

/**
 * Join table linking a playlist to the tracks it contains. Tracks are
 * referenced by their document URI string.
 *
 * position allows tracks within a playlist to be manually reordered.
 */
@Entity(
    tableName = "playlist_track_cross_ref",
    primaryKeys = ["playlistId", "trackId"]
)
data class PlaylistTrackCrossRef(
    val playlistId: Long,
    val trackId: String,
    val position: Int
)
