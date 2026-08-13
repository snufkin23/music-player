package com.sushantkhadka.musicplayer.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Marks a MediaStore track as favorited. Kept as its own table rather
 * than a boolean column on some Track entity, since tracks themselves
 * aren't stored in Room (MediaStore is the source of truth).
 */
@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    val mediaStoreTrackId: Long,

    val addedAt: Long = System.currentTimeMillis()
)
