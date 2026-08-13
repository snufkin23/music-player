package com.sushantkhadka.musicplayer.data.local.database.entity

import androidx.room.Entity

/**
 * Join table linking a playlist to the tracks it contains. Tracks are
 * referenced by their MediaStore ID (mediaStoreTrackId), not stored
 * directly, since MediaStore remains the source of truth for actual
 * audio files.
 *
 * position allows tracks within a playlist to be manually reordered.
 */
@Entity(
    tableName = "playlist_track_cross_ref",
    primaryKeys = ["playlistId", "mediaStoreTrackId"]
)
data class PlaylistTrackCrossRef(
    val playlistId: Long,
    val mediaStoreTrackId: Long,
    val position: Int
)
