package com.sushantkhadka.musicplayer.domain.repository

import android.net.Uri
import com.sushantkhadka.musicplayer.domain.model.MusicFolder
import kotlinx.coroutines.flow.Flow

interface FolderRepository {

    fun observeFolders(): Flow<List<MusicFolder>>

    suspend fun addFolder(uri: Uri)

    suspend fun removeFolder(folder: MusicFolder)
}
