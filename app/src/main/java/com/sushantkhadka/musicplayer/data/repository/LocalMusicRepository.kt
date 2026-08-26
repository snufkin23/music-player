package com.sushantkhadka.musicplayer.data.repository

import android.net.Uri
import com.sushantkhadka.musicplayer.data.local.database.dao.FavoriteDao
import com.sushantkhadka.musicplayer.data.local.database.dao.TrackCacheDao
import com.sushantkhadka.musicplayer.data.local.database.dao.TrackStatsDao
import com.sushantkhadka.musicplayer.data.local.database.entity.FavoriteEntity
import com.sushantkhadka.musicplayer.data.local.database.entity.TrackEntity
import com.sushantkhadka.musicplayer.data.local.database.entity.TrackStatsEntity
import com.sushantkhadka.musicplayer.data.local.folderscanner.FolderMusicDataSource
import com.sushantkhadka.musicplayer.domain.model.Album
import com.sushantkhadka.musicplayer.domain.model.Artist
import com.sushantkhadka.musicplayer.domain.model.Track
import com.sushantkhadka.musicplayer.domain.repository.FolderRepository
import com.sushantkhadka.musicplayer.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalMusicRepository @Inject constructor(
    private val folderDataSource: FolderMusicDataSource,
    private val trackCacheDao: TrackCacheDao,
    private val favoriteDao: FavoriteDao,
    private val trackStatsDao: TrackStatsDao,
    private val folderRepository: FolderRepository
) : MusicRepository {

    private var syncedFolderUris: Set<String>? = null

    private val _libraryRefreshSignal = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    override val libraryRefreshSignal: Flow<Unit> = _libraryRefreshSignal

    override suspend fun getAllTracks(): List<Track> {
        syncIfNeeded()
        return trackCacheDao.getAll().map { it.toModel() }
    }

    override suspend fun getTracksForAlbum(albumKey: String): List<Track> {
        syncIfNeeded()
        return trackCacheDao.getByAlbum(albumKey).map { it.toModel() }
    }

    override suspend fun getTracksForArtist(artistKey: String): List<Track> {
        syncIfNeeded()
        return trackCacheDao.getByArtist(artistKey).map { it.toModel() }
    }

    override suspend fun searchTracks(query: String): List<Track> {
        syncIfNeeded()
        val q = query.trim().lowercase()
        if (q.isEmpty()) return emptyList()
        return trackCacheDao.getAll()
            .map { it.toModel() }
            .filter {
                it.title.lowercase().contains(q) ||
                    it.artist.lowercase().contains(q) ||
                    it.album.lowercase().contains(q)
            }
    }

    override suspend fun getAllAlbums(): List<Album> {
        syncIfNeeded()
        return trackCacheDao.getAll()
            .map { it.toModel() }
            .groupBy { it.albumKey }
            .map { (key, group) ->
                Album(
                    key = key,
                    name = group.first().album,
                    artist = group.first().artist,
                    trackCount = group.size,
                    albumArtUri = group.first().uri
                )
            }
            .sortedBy { it.name.lowercase() }
    }

    override suspend fun getAllArtists(): List<Artist> {
        syncIfNeeded()
        return trackCacheDao.getAll()
            .map { it.toModel() }
            .groupBy { it.artistKey }
            .map { (key, group) ->
                Artist(
                    key = key,
                    name = group.first().artist,
                    albumCount = group.map { it.albumKey }.distinct().size,
                    trackCount = group.size
                )
            }
            .sortedBy { it.name.lowercase() }
    }

    override fun getFavoriteTrackIds(): Flow<List<String>> {
        return favoriteDao.getAllFavorites().map { favorites ->
            favorites.map { it.trackId }
        }
    }

    override suspend fun toggleFavorite(trackId: String) {
        val currentlyFavorite = favoriteDao.isFavorite(trackId).first()
        if (currentlyFavorite) {
            favoriteDao.removeFavorite(FavoriteEntity(trackId = trackId))
        } else {
            favoriteDao.addFavorite(FavoriteEntity(trackId = trackId))
        }
    }

    override fun isFavorite(trackId: String): Flow<Boolean> {
        return favoriteDao.isFavorite(trackId)
    }

    override suspend fun recordTrackPlayed(trackId: String) {
        val existingStats = trackStatsDao.getStatsForTrack(trackId)
        if (existingStats == null) {
            trackStatsDao.upsertStats(
                TrackStatsEntity(trackId = trackId, playCount = 1)
            )
        } else {
            trackStatsDao.incrementPlayCount(trackId)
        }
    }

    override fun getRecentlyPlayedTrackIds(limit: Int): Flow<List<String>> {
        return trackStatsDao.getRecentlyPlayed(limit).map { stats ->
            stats.map { it.trackId }
        }
    }

    override fun getMostPlayedTrackIds(limit: Int): Flow<List<String>> {
        return trackStatsDao.getMostPlayed(limit).map { stats ->
            stats.map { it.trackId }
        }
    }

    override suspend fun rescanLibrary() {
        val folderUris = folderRepository.observeFolders().first().map { it.uri }.toSet()
        scan(folderUris)
        _libraryRefreshSignal.emit(Unit)
    }

    private suspend fun syncIfNeeded() {
        val folderUris = folderRepository.observeFolders().first().map { it.uri }.toSet()
        if (syncedFolderUris == folderUris) return
        scan(folderUris)
    }

    private suspend fun scan(folderUris: Set<String>) {
        val tracks = folderDataSource.scan()
        trackCacheDao.clear()
        trackCacheDao.insertAll(tracks.map { it.toEntity() })
        syncedFolderUris = folderUris
    }

    private fun TrackEntity.toModel(): Track = Track(
        id = id,
        uri = Uri.parse(uri),
        title = title,
        artist = artist,
        album = album,
        durationMs = durationMs,
        trackNumber = trackNumber,
        albumKey = albumKey,
        artistKey = artistKey
    )

    private fun Track.toEntity(): TrackEntity = TrackEntity(
        id = id,
        uri = uri.toString(),
        title = title,
        artist = artist,
        album = album,
        durationMs = durationMs,
        trackNumber = trackNumber,
        albumKey = albumKey,
        artistKey = artistKey
    )
}
