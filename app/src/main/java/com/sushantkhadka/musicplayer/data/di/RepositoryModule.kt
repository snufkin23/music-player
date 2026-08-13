package com.sushantkhadka.musicplayer.data.di

import com.sushantkhadka.musicplayer.data.repository.LocalMusicRepository
import com.sushantkhadka.musicplayer.data.repository.MusicRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Binds MusicRepository to its current implementation. This is the
 * single line that will change when a future RemoteMusicRepository
 * or HybridMusicRepository replaces or joins LocalMusicRepository —
 * no ViewModel or UI code needs to change alongside it.
 *
 * @Binds (not @Provides) is used here since LocalMusicRepository has
 * an @Inject constructor already — Binds is the more efficient choice
 * for simple interface-to-implementation mapping.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMusicRepository(
        impl: LocalMusicRepository
    ): MusicRepository
}
