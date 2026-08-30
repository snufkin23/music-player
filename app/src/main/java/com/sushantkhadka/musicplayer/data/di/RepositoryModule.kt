package com.sushantkhadka.musicplayer.data.di

import com.sushantkhadka.musicplayer.data.repository.LocalMusicRepository
import com.sushantkhadka.musicplayer.data.repository.MediaControllerPlaybackRepository
import com.sushantkhadka.musicplayer.data.repository.SafFolderRepository
import com.sushantkhadka.musicplayer.domain.repository.FolderRepository
import com.sushantkhadka.musicplayer.domain.repository.MusicRepository
import com.sushantkhadka.musicplayer.domain.repository.PlaybackRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Binds domain repository interfaces to their data-layer implementations.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMusicRepository(
        impl: LocalMusicRepository
    ): MusicRepository

    @Binds
    @Singleton
    abstract fun bindFolderRepository(
        impl: SafFolderRepository
    ): FolderRepository

    @Binds
    @Singleton
    abstract fun bindPlaybackRepository(
        impl: MediaControllerPlaybackRepository
    ): PlaybackRepository
}
