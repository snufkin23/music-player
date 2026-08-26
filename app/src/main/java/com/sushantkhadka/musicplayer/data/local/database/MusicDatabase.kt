package com.sushantkhadka.musicplayer.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sushantkhadka.musicplayer.data.local.database.dao.FavoriteDao
import com.sushantkhadka.musicplayer.data.local.database.dao.MusicFolderDao
import com.sushantkhadka.musicplayer.data.local.database.dao.PlaylistDao
import com.sushantkhadka.musicplayer.data.local.database.dao.TrackCacheDao
import com.sushantkhadka.musicplayer.data.local.database.dao.TrackStatsDao
import com.sushantkhadka.musicplayer.data.local.database.entity.FavoriteEntity
import com.sushantkhadka.musicplayer.data.local.database.entity.MusicFolderEntity
import com.sushantkhadka.musicplayer.data.local.database.entity.PlaylistEntity
import com.sushantkhadka.musicplayer.data.local.database.entity.PlaylistTrackCrossRef
import com.sushantkhadka.musicplayer.data.local.database.entity.TrackEntity
import com.sushantkhadka.musicplayer.data.local.database.entity.TrackStatsEntity

/**
 * App's Room database. Holds app-specific metadata (playlists,
 * favorites, play stats), the user-selected music folders, and a cache
 * of scanned tracks. Audio files themselves are read from the folders
 * the user grants access to via the Storage Access Framework.
 */
@Database(
    entities = [
        PlaylistEntity::class,
        PlaylistTrackCrossRef::class,
        FavoriteEntity::class,
        TrackStatsEntity::class,
        MusicFolderEntity::class,
        TrackEntity::class
    ],
    version = 3,
    exportSchema = true
)
abstract class MusicDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun trackStatsDao(): TrackStatsDao
    abstract fun musicFolderDao(): MusicFolderDao
    abstract fun trackCacheDao(): TrackCacheDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS music_folders (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "uri TEXT NOT NULL, " +
                        "displayName TEXT NOT NULL, " +
                        "addedAt INTEGER NOT NULL)"
                )
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS tracks (" +
                        "id TEXT NOT NULL, " +
                        "uri TEXT NOT NULL, " +
                        "title TEXT NOT NULL, " +
                        "artist TEXT NOT NULL, " +
                        "album TEXT NOT NULL, " +
                        "durationMs INTEGER NOT NULL, " +
                        "trackNumber INTEGER, " +
                        "albumKey TEXT NOT NULL, " +
                        "artistKey TEXT NOT NULL, " +
                        "PRIMARY KEY(id))"
                )

                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS favorites_new (" +
                        "trackId TEXT NOT NULL, " +
                        "addedAt INTEGER NOT NULL, " +
                        "PRIMARY KEY(trackId))"
                )
                db.execSQL(
                    "INSERT INTO favorites_new (trackId, addedAt) " +
                        "SELECT CAST(mediaStoreTrackId AS TEXT), addedAt FROM favorites"
                )
                db.execSQL("DROP TABLE favorites")
                db.execSQL("ALTER TABLE favorites_new RENAME TO favorites")

                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS track_stats_new (" +
                        "trackId TEXT NOT NULL, " +
                        "playCount INTEGER NOT NULL, " +
                        "lastPlayedAt INTEGER NOT NULL, " +
                        "PRIMARY KEY(trackId))"
                )
                db.execSQL(
                    "INSERT INTO track_stats_new (trackId, playCount, lastPlayedAt) " +
                        "SELECT CAST(mediaStoreTrackId AS TEXT), playCount, lastPlayedAt FROM track_stats"
                )
                db.execSQL("DROP TABLE track_stats")
                db.execSQL("ALTER TABLE track_stats_new RENAME TO track_stats")

                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS playlist_track_cross_ref_new (" +
                        "playlistId INTEGER NOT NULL, " +
                        "trackId TEXT NOT NULL, " +
                        "position INTEGER NOT NULL, " +
                        "PRIMARY KEY(playlistId, trackId))"
                )
                db.execSQL(
                    "INSERT INTO playlist_track_cross_ref_new (playlistId, trackId, position) " +
                        "SELECT playlistId, CAST(mediaStoreTrackId AS TEXT), position FROM playlist_track_cross_ref"
                )
                db.execSQL("DROP TABLE playlist_track_cross_ref")
                db.execSQL("ALTER TABLE playlist_track_cross_ref_new RENAME TO playlist_track_cross_ref")
            }
        }
    }
}
