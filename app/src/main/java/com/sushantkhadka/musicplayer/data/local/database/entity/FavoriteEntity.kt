package com.sushantkhadka.musicplayer.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Marks a track as favorited, keyed by its document URI string.
 */
@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    val trackId: String,

    val addedAt: Long = System.currentTimeMillis()
)
