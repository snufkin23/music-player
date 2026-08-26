package com.sushantkhadka.musicplayer.data.di

import android.content.Context
import androidx.room.Room
import com.sushantkhadka.musicplayer.data.local.database.MusicDatabase
import com.sushantkhadka.musicplayer.data.local.database.dao.FavoriteDao
import com.sushantkhadka.musicplayer.data.local.database.dao.MusicFolderDao
import com.sushantkhadka.musicplayer.data.local.database.dao.PlaylistDao
import com.sushantkhadka.musicplayer.data.local.database.dao.TrackCacheDao
import com.sushantkhadka.musicplayer.data.local.database.dao.TrackStatsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMusicDatabase(
        @ApplicationContext context: Context
    ): MusicDatabase {
        return Room.databaseBuilder(
            context,
            MusicDatabase::class.java,
            "musicplayer.db"
        ).addMigrations(MusicDatabase.MIGRATION_1_2, MusicDatabase.MIGRATION_2_3)
            .build()
    }

    @Provides
    fun providePlaylistDao(database: MusicDatabase): PlaylistDao = database.playlistDao()

    @Provides
    fun provideFavoriteDao(database: MusicDatabase): FavoriteDao = database.favoriteDao()

    @Provides
    fun provideTrackStatsDao(database: MusicDatabase): TrackStatsDao = database.trackStatsDao()

    @Provides
    fun provideMusicFolderDao(database: MusicDatabase): MusicFolderDao = database.musicFolderDao()

    @Provides
    fun provideTrackCacheDao(database: MusicDatabase): TrackCacheDao = database.trackCacheDao()
}
