package com.sushantkhadka.musicplayer.data.repository

import com.sushantkhadka.musicplayer.data.local.database.dao.FavoriteDao
import com.sushantkhadka.musicplayer.data.local.database.dao.TrackStatsDao
import com.sushantkhadka.musicplayer.data.local.database.entity.FavoriteEntity
import com.sushantkhadka.musicplayer.data.local.database.entity.TrackStatsEntity
import com.sushantkhadka.musicplayer.data.local.mediastore.MediaStoreDataSource
import com.sushantkhadka.musicplayer.data.model.Album
import com.sushantkhadka.musicplayer.data.model.Artist
import com.sushantkhadka.musicplayer.data.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Local-only implementation of MusicRepository, backed by MediaStore
 * (tracks/albums/artists) and Room (favorites, play stats). This is
 * the sole implementation for Phase 1. A future RemoteMusicRepository
 * or HybridMusicRepository can be added later and swapped in via the
 * Hilt binding without any ViewModel changes.
 */
class LocalMusicRepository @Inject constructor(
    private val mediaStoreDataSource: MediaStoreDataSource,
    private val favoriteDao: FavoriteDao,
    private val trackStatsDao: TrackStatsDao
) : MusicRepository {

    override suspend fun getAllTracks(): List<Track> {
        return mediaStoreDataSource.getAllTracks()
    }

    override suspend fun getAllAlbums(): List<Album> {
        return mediaStoreDataSource.getAllAlbums()
    }

    override suspend fun getAllArtists(): List<Artist> {
        return mediaStoreDataSource.getAllArtists()
    }

    override fun getFavoriteTrackIds(): Flow<List<Long>> {
        return favoriteDao.getAllFavorites().map { favorites ->
            favorites.map { it.mediaStoreTrackId }
        }
    }

    override suspend fun toggleFavorite(trackId: Long) {
        val currentlyFavorite = favoriteDao.isFavorite(trackId).first()
        if (currentlyFavorite) {
            favoriteDao.removeFavorite(FavoriteEntity(mediaStoreTrackId = trackId))
        } else {
            favoriteDao.addFavorite(FavoriteEntity(mediaStoreTrackId = trackId))
        }
    }

    override fun isFavorite(trackId: Long): Flow<Boolean> {
        return favoriteDao.isFavorite(trackId)
    }

    override suspend fun recordTrackPlayed(trackId: Long) {
        val existingStats = trackStatsDao.getStatsForTrack(trackId)
        if (existingStats == null) {
            trackStatsDao.upsertStats(
                TrackStatsEntity(mediaStoreTrackId = trackId, playCount = 1)
            )
        } else {
            trackStatsDao.incrementPlayCount(trackId)
        }
    }

    override fun getRecentlyPlayedTrackIds(limit: Int): Flow<List<Long>> {
        return trackStatsDao.getRecentlyPlayed(limit).map { stats ->
            stats.map { it.mediaStoreTrackId }
        }
    }

    override fun getMostPlayedTrackIds(limit: Int): Flow<List<Long>> {
        return trackStatsDao.getMostPlayed(limit).map { stats ->
            stats.map { it.mediaStoreTrackId }
        }
    }
}
