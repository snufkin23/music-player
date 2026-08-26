package com.sushantkhadka.musicplayer.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sushantkhadka.musicplayer.data.local.database.entity.MusicFolderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicFolderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(folder: MusicFolderEntity)

    @Query("DELETE FROM music_folders WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT * FROM music_folders ORDER BY addedAt ASC")
    fun observeAll(): Flow<List<MusicFolderEntity>>
}
