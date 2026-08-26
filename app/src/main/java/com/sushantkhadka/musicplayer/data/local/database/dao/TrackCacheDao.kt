package com.sushantkhadka.musicplayer.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sushantkhadka.musicplayer.data.local.database.entity.TrackEntity

@Dao
interface TrackCacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tracks: List<TrackEntity>)

    @Query("DELETE FROM tracks")
    suspend fun clear()

    @Query("SELECT COUNT(*) FROM tracks")
    suspend fun count(): Int

    @Query("SELECT * FROM tracks ORDER BY title COLLATE NOCASE ASC")
    suspend fun getAll(): List<TrackEntity>

    @Query("SELECT * FROM tracks WHERE albumKey = :albumKey ORDER BY trackNumber ASC")
    suspend fun getByAlbum(albumKey: String): List<TrackEntity>

    @Query("SELECT * FROM tracks WHERE artistKey = :artistKey ORDER BY album COLLATE NOCASE ASC, trackNumber ASC")
    suspend fun getByArtist(artistKey: String): List<TrackEntity>
}
