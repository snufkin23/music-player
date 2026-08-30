package com.sushantkhadka.musicplayer.data.imageloading

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import coil3.ImageLoader
import coil3.asImage
import coil3.decode.DataSource
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.ImageFetchResult
import coil3.request.Options
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * Extracts embedded album art from a track's document URI, caching the
 * raw artwork to disk keyed by the URI so it is not re-read on every
 * cold start or after Coil's own cache is evicted.
 */
class AlbumArtFetcher(
    private val data: AlbumArtRequest,
    private val options: Options
) : Fetcher {

    override suspend fun fetch(): FetchResult? {
        val uri = data.uri ?: return null
        val context = options.context

        val bitmap: Bitmap? = withContext(Dispatchers.IO) {
            loadOrExtract(context, uri)
        }

        return bitmap?.let {
            ImageFetchResult(
                image = it.asImage(),
                isSampled = false,
                dataSource = DataSource.DISK
            )
        }
    }

    private fun loadOrExtract(context: Context, uri: Uri): Bitmap? {
        val cacheFile = cacheFile(context, uri)
        if (cacheFile.exists()) {
            return BitmapFactory.decodeFile(cacheFile.absolutePath)
        }

        val extracted = extract(context, uri) ?: return null
        runCatching {
            cacheFile.parentFile?.mkdirs()
            FileOutputStream(cacheFile).use { out ->
                extracted.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
        }
        return extracted
    }

    private fun extract(context: Context, uri: Uri): Bitmap? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(context, uri)
            retriever.embeddedPicture?.let { bytes ->
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            }
        } catch (e: Exception) {
            null
        } finally {
            runCatching { retriever.release() }
        }
    }

    private fun cacheFile(context: Context, uri: Uri): File =
        File(File(context.cacheDir, "album_art"), "${uri.hashCode()}")

    class Factory : Fetcher.Factory<AlbumArtRequest> {
        override fun create(
            data: AlbumArtRequest,
            options: Options,
            imageLoader: ImageLoader
        ): Fetcher {
            return AlbumArtFetcher(data, options)
        }
    }
}
