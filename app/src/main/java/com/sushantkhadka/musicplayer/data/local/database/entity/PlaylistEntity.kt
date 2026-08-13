package com.sushantkhadka.musicplayer.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room table representing a user-created playlist.
 *
 * Note: this table does NOT store track data directly. Actual audio
 * files are sourced from MediaStore (the source of truth for what
 * tracks exist on-device). This entity only stores playlist metadata;
 * the link between a playlist and its tracks lives in a separate
 * PlaylistTrackCrossRef table.
 */
@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,

    val createdAt: Long = System.currentTimeMillis(),

    val updatedAt: Long = System.currentTimeMillis()
)
