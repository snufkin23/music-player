package com.sushantkhadka.musicplayer.domain.model

data class MusicFolder(
    val id: Long,
    val uri: String,
    val displayName: String,
    val addedAt: Long
)
