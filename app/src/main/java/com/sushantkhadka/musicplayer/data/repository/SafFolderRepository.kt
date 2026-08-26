package com.sushantkhadka.musicplayer.data.repository

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import com.sushantkhadka.musicplayer.data.local.database.dao.MusicFolderDao
import com.sushantkhadka.musicplayer.data.local.database.entity.MusicFolderEntity
import com.sushantkhadka.musicplayer.domain.model.MusicFolder
import com.sushantkhadka.musicplayer.domain.repository.FolderRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SafFolderRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val folderDao: MusicFolderDao
) : FolderRepository {

    override fun observeFolders(): Flow<List<MusicFolder>> =
        folderDao.observeAll().map { entities ->
            entities.map { it.toModel() }
        }

    override suspend fun addFolder(uri: Uri) {
        runCatching {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
        val displayName = queryDisplayName(uri) ?: uri.lastPathSegment ?: "Music"
        folderDao.insert(
            MusicFolderEntity(uri = uri.toString(), displayName = displayName)
        )
    }

    override suspend fun removeFolder(folder: MusicFolder) {
        folderDao.delete(folder.id)
        runCatching {
            context.contentResolver.releasePersistableUriPermission(
                Uri.parse(folder.uri),
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
    }

    private fun queryDisplayName(uri: Uri): String? {
        return context.contentResolver.query(
            uri,
            arrayOf(DocumentsContract.Document.COLUMN_DISPLAY_NAME),
            null,
            null,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) cursor.getString(0) else null
        }
    }

    private fun MusicFolderEntity.toModel(): MusicFolder = MusicFolder(
        id = id,
        uri = uri,
        displayName = displayName,
        addedAt = addedAt
    )
}
