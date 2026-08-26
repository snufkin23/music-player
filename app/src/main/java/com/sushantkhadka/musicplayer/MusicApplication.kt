package com.sushantkhadka.musicplayer

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.request.crossfade
import com.sushantkhadka.musicplayer.data.imageloading.AlbumArtFetcher
import com.sushantkhadka.musicplayer.data.imageloading.AlbumArtKeyer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MusicApplication : Application(), SingletonImageLoader.Factory {

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .components {
                add(AlbumArtKeyer())
                add(AlbumArtFetcher.Factory())
            }
            .crossfade(true)
            .build()
    }
}
