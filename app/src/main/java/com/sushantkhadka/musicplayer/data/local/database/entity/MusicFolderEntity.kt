package com.sushantkhadka.musicplayer.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A user-selected folder (via Storage Access Framework) that the app is
 * permitted to scan for music. The `uri` is the persisted tree URI.
 */
@Entity(tableName = "music_folders")
data class MusicFolderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val uri: String,

    val displayName: String,

    val addedAt: Long = System.currentTimeMillis()
)
