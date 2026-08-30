package com.sushantkhadka.musicplayer.ui.player

import androidx.media3.common.MediaItem

data class QueueItemUi(
    val mediaId: String,
    val title: String,
    val artist: String
)

fun MediaItem.toQueueItemUi(): QueueItemUi {
    return QueueItemUi(
        mediaId = mediaId,
        title = mediaMetadata.title?.toString() ?: "",
        artist = mediaMetadata.artist?.toString() ?: ""
    )
}
