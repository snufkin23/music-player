package com.sushantkhadka.musicplayer.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sushantkhadka.musicplayer.data.local.database.dao.FavoriteDao
import com.sushantkhadka.musicplayer.data.local.database.dao.PlaylistDao
import com.sushantkhadka.musicplayer.data.local.database.dao.TrackStatsDao
import com.sushantkhadka.musicplayer.data.local.database.entity.FavoriteEntity
import com.sushantkhadka.musicplayer.data.local.database.entity.PlaylistEntity
import com.sushantkhadka.musicplayer.data.local.database.entity.PlaylistTrackCrossRef
import com.sushantkhadka.musicplayer.data.local.database.entity.TrackStatsEntity

/**
 * App's Room database. Holds only app-specific metadata (playlists,
 * favorites, play stats) — actual audio file data is sourced from
 * MediaStore, never duplicated here.
 *
 * version starts at 1. Any future schema change requires bumping this
 * and providing a Migration (or, during early development before any
 * real user data exists, fallbackToDestructiveMigration is acceptable).
 */
@Database(
    entities = [
        PlaylistEntity::class,
        PlaylistTrackCrossRef::class,
        FavoriteEntity::class,
        TrackStatsEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class MusicDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun trackStatsDao(): TrackStatsDao
}
