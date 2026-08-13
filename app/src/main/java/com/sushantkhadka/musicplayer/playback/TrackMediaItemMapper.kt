package com.sushantkhadka.musicplayer.playback

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.sushantkhadka.musicplayer.data.model.Track

/**
 * Maps this app's domain Track model to a Media3 MediaItem. This is
 * the ONLY place this conversion happens — Track never gets built
 * into a MediaItem anywhere else, keeping the seam between domain
 * models and Media3 types in one place.
 */
fun Track.toMediaItem(): MediaItem {
    val metadata = MediaMetadata.Builder()
        .setTitle(title)
        .setArtist(artist)
        .setAlbumTitle(album)
        .build()

    return MediaItem.Builder()
        .setMediaId(id.toString())
        .setUri(uri)
        .setMediaMetadata(metadata)
        .build()
}
