package com.sushantkhadka.musicplayer.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sushantkhadka.musicplayer.data.local.database.entity.PlaylistEntity
import com.sushantkhadka.musicplayer.data.local.database.entity.PlaylistTrackCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    @Delete
    suspend fun deletePlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlists ORDER BY updatedAt DESC")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlists WHERE id = :playlistId")
    fun getPlaylistById(playlistId: Long): Flow<PlaylistEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTrackToPlaylist(crossRef: PlaylistTrackCrossRef)

    @Query(
        "DELETE FROM playlist_track_cross_ref " +
            "WHERE playlistId = :playlistId AND mediaStoreTrackId = :trackId"
    )
    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long)

    @Query(
        "SELECT mediaStoreTrackId FROM playlist_track_cross_ref " +
            "WHERE playlistId = :playlistId ORDER BY position ASC"
    )
    fun getTrackIdsForPlaylist(playlistId: Long): Flow<List<Long>>
}
