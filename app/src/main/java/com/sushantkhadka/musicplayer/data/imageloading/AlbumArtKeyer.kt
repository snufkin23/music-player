package com.sushantkhadka.musicplayer.data.imageloading

import coil3.key.Keyer
import coil3.request.Options

class AlbumArtKeyer : Keyer<AlbumArtRequest> {
    override fun key(data: AlbumArtRequest, options: Options): String {
        return "album_art_${data.uri}"
    }
}
