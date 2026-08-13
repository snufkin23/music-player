package com.sushantkhadka.musicplayer.data.di

import android.content.Context
import androidx.room.Room
import com.sushantkhadka.musicplayer.data.local.database.MusicDatabase
import com.sushantkhadka.musicplayer.data.local.database.dao.FavoriteDao
import com.sushantkhadka.musicplayer.data.local.database.dao.PlaylistDao
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
        ).build()
    }

    @Provides
    fun providePlaylistDao(database: MusicDatabase): PlaylistDao = database.playlistDao()

    @Provides
    fun provideFavoriteDao(database: MusicDatabase): FavoriteDao = database.favoriteDao()

    @Provides
    fun provideTrackStatsDao(database: MusicDatabase): TrackStatsDao = database.trackStatsDao()
}
