package com.sushantkhadka.musicplayer.data.local.folderscanner

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.DocumentsContract
import com.sushantkhadka.musicplayer.domain.model.Track
import com.sushantkhadka.musicplayer.domain.repository.FolderRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Scans the user-selected folders (Storage Access Framework tree URIs)
 * recursively for audio files and extracts their metadata from embedded
 * tags. Only music extensions are accepted, and a small set of known
 * non-music folders (Voice Notes, Ringtones, etc.) is skipped.
 */
class FolderMusicDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val folderRepository: FolderRepository
) {

    private val allowedExtensions = setOf("mp3", "m4a", "aac", "flac", "wav", "ogg", "opus")

    private val blockedFolders = setOf(
        "voice notes",
        "ringtones",
        "notifications",
        "alarms",
        "android",
        "audio"
    )

    suspend fun scan(): List<Track> = withContext(Dispatchers.IO) {
        val folders = folderRepository.observeFolders().first()
        val tracks = mutableListOf<Track>()
        for (folder in folders) {
            runCatching {
                val treeUri = Uri.parse(folder.uri)
                val rootDocId = DocumentsContract.getTreeDocumentId(treeUri)
                scanDirectory(treeUri, rootDocId, tracks)
            }
        }
        tracks
    }

    private fun scanDirectory(treeUri: Uri, dirDocId: String, out: MutableList<Track>) {
        val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(treeUri, dirDocId)
        val projection = arrayOf(
            DocumentsContract.Document.COLUMN_DOCUMENT_ID,
            DocumentsContract.Document.COLUMN_DISPLAY_NAME,
            DocumentsContract.Document.COLUMN_MIME_TYPE
        )

        val cursor = context.contentResolver.query(
            childrenUri,
            projection,
            null,
            null,
            null
        ) ?: return

        val docIdCol = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DOCUMENT_ID)
        val nameCol = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
        val mimeCol = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_MIME_TYPE)

        val subDirectories = mutableListOf<Pair<String, String>>()

        while (cursor.moveToNext()) {
            val docId = cursor.getString(docIdCol)
            val name = cursor.getString(nameCol) ?: continue
            val mimeType = cursor.getString(mimeCol)

            if (mimeType == DocumentsContract.Document.MIME_TYPE_DIR) {
                subDirectories.add(docId to name)
            } else if (isSupportedAudio(name)) {
                val fileUri = DocumentsContract.buildDocumentUriUsingTree(treeUri, docId)
                buildTrack(fileUri, name)?.let(out::add)
            }
        }
        cursor.close()

        for ((subDocId, subName) in subDirectories) {
            if (subName.trim().lowercase() in blockedFolders) continue
            scanDirectory(treeUri, subDocId, out)
        }
    }

    private fun isSupportedAudio(name: String): Boolean {
        val extension = name.substringAfterLast('.', "").lowercase()
        return extension in allowedExtensions
    }

    private fun buildTrack(uri: Uri, fileName: String): Track? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(context, uri)

            val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                ?.trim()
                .orUnknown(fileName.substringBeforeLast('.'))
            val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                ?.trim()
                .orUnknown("Unknown Artist")
            val album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
                ?.trim()
                .orUnknown("Unknown Album")
            val durationMs = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toLongOrNull() ?: 0L
            val trackNumber = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER)
                ?.substringBefore('/')
                ?.toIntOrNull()
                ?.takeIf { it > 0 }

            Track(
                id = uri.toString(),
                uri = uri,
                title = title,
                artist = artist,
                album = album,
                durationMs = durationMs,
                trackNumber = trackNumber,
                albumKey = albumKeyOf(album, artist),
                artistKey = artistKeyOf(artist)
            )
        } catch (e: Exception) {
            null
        } finally {
            retriever.release()
        }
    }

    private fun String?.orUnknown(default: String): String =
        if (this.isNullOrBlank()) default else this

    private fun artistKeyOf(artist: String): String = artist.trim().ifEmpty { "Unknown Artist" }

    private fun albumKeyOf(album: String, artist: String): String =
        "${artistKeyOf(artist)}|${album.trim().ifEmpty { "Unknown Album" }}"
}
