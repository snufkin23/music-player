package com.sushantkhadka.musicplayer.data.repository

import com.sushantkhadka.musicplayer.data.model.Album
import com.sushantkhadka.musicplayer.data.model.Artist
import com.sushantkhadka.musicplayer.data.model.Track
import kotlinx.coroutines.flow.Flow

/**
 * Single source of truth for music library data, as far as ViewModels
 * are concerned. ViewModels depend on this interface only — never on
 * MediaStoreDataSource or Room DAOs directly. This is the seam that
 * lets a future RemoteMusicRepository (streaming) or
 * HybridMusicRepository slot in later without touching any UI code.
 */
interface MusicRepository {

    suspend fun getAllTracks(): List<Track>

    suspend fun getAllAlbums(): List<Album>

    suspend fun getAllArtists(): List<Artist>

    fun getFavoriteTrackIds(): Flow<List<Long>>

    suspend fun toggleFavorite(trackId: Long)

    fun isFavorite(trackId: Long): Flow<Boolean>

    suspend fun recordTrackPlayed(trackId: Long)

    fun getRecentlyPlayedTrackIds(limit: Int = 50): Flow<List<Long>>

    fun getMostPlayedTrackIds(limit: Int = 50): Flow<List<Long>>
}
