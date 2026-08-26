package com.sushantkhadka.musicplayer.domain.repository

import com.sushantkhadka.musicplayer.domain.model.Album
import com.sushantkhadka.musicplayer.domain.model.Artist
import com.sushantkhadka.musicplayer.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface MusicRepository {

    val libraryRefreshSignal: Flow<Unit>

    suspend fun getAllTracks(): List<Track>

    suspend fun getTracksForAlbum(albumKey: String): List<Track>

    suspend fun getTracksForArtist(artistKey: String): List<Track>

    suspend fun searchTracks(query: String): List<Track>

    suspend fun getAllAlbums(): List<Album>

    suspend fun getAllArtists(): List<Artist>

    fun getFavoriteTrackIds(): Flow<List<String>>

    suspend fun toggleFavorite(trackId: String)

    fun isFavorite(trackId: String): Flow<Boolean>

    suspend fun recordTrackPlayed(trackId: String)

    fun getRecentlyPlayedTrackIds(limit: Int = 50): Flow<List<String>>

    fun getMostPlayedTrackIds(limit: Int = 50): Flow<List<String>>

    suspend fun rescanLibrary()
}
