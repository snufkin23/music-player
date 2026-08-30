package com.sushantkhadka.musicplayer.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sushantkhadka.musicplayer.data.local.database.entity.TrackStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackStatsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStats(stats: TrackStatsEntity)

    @Query("DELETE FROM track_stats WHERE trackId = :trackId")
    suspend fun delete(trackId: String)

    @Query("SELECT * FROM track_stats")
    suspend fun getAllOnce(): List<TrackStatsEntity>

    @Query("SELECT * FROM track_stats WHERE trackId = :trackId")
    suspend fun getStatsForTrack(trackId: String): TrackStatsEntity?

    @Query("SELECT * FROM track_stats ORDER BY lastPlayedAt DESC LIMIT :limit")
    fun getRecentlyPlayed(limit: Int = 50): Flow<List<TrackStatsEntity>>

    @Query("SELECT * FROM track_stats ORDER BY playCount DESC LIMIT :limit")
    fun getMostPlayed(limit: Int = 50): Flow<List<TrackStatsEntity>>

    @Query(
        "UPDATE track_stats SET playCount = playCount + 1, lastPlayedAt = :timestamp " +
            "WHERE trackId = :trackId"
    )
    suspend fun incrementPlayCount(trackId: String, timestamp: Long = System.currentTimeMillis())
}
