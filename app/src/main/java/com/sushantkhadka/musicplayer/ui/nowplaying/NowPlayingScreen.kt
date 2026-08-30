package com.sushantkhadka.musicplayer.ui.nowplaying

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import coil3.compose.AsyncImage
import com.sushantkhadka.musicplayer.R
import com.sushantkhadka.musicplayer.data.imageloading.AlbumArtRequest
import com.sushantkhadka.musicplayer.domain.model.RepeatMode
import com.sushantkhadka.musicplayer.ui.theme.AppTextStyles
import com.sushantkhadka.musicplayer.ui.theme.Dimens
import java.util.Locale
import java.util.concurrent.TimeUnit
import androidx.core.net.toUri

@Composable
fun NowPlayingScreen(
    title: String,
    artist: String,
    artUri: String?,
    isPlaying: Boolean,
    isShuffleEnabled: Boolean,
    repeatMode: RepeatMode,
    currentPositionMs: Long,
    durationMs: Long,
    onPlayPauseClicked: () -> Unit,
    onSkipNextClicked: () -> Unit,
    onSkipPreviousClicked: () -> Unit,
    onShuffleClicked: () -> Unit,
    onRepeatClicked: () -> Unit,
    onSeek: (Long) -> Unit,
    onQueueClicked: () -> Unit,
    onCollapse: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.spaceSmall),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onCollapse) {
                    Icon(
                        Icons.Filled.KeyboardArrowDown,
                        contentDescription = stringResource(R.string.action_collapse)
                    )
                }
                IconButton(onClick = onQueueClicked) {
                    Icon(
						Icons.AutoMirrored.Filled.QueueMusic,
                        contentDescription = stringResource(R.string.action_queue)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.spaceXXLarge),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AsyncImage(
                    model = artUri?.let { AlbumArtRequest(it.toUri()) },
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    error = ColorPainter(MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier
                        .size(Dimens.albumArtNowPlaying)
                        .clip(RoundedCornerShape(Dimens.cornerRadiusLarge))
                )

                Spacer(modifier = Modifier.height(Dimens.spaceXXLarge))

                Text(
                    text = title.ifEmpty { stringResource(R.string.nothing_playing) },
                    style = AppTextStyles.songTitle,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(Dimens.spaceXSmall))
                Text(
                    text = artist,
                    style = AppTextStyles.itemTitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            Column(modifier = Modifier.padding(horizontal = Dimens.spaceXLarge)) {
                SeekBar(
                    currentPositionMs = currentPositionMs,
                    durationMs = durationMs,
                    onSeek = onSeek
                )

                Spacer(modifier = Modifier.height(Dimens.spaceSmall))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatDuration(currentPositionMs),
                        style = AppTextStyles.caption
                    )
                    Text(
                        text = formatDuration(durationMs),
                        style = AppTextStyles.caption
                    )
                }

                Spacer(modifier = Modifier.height(Dimens.spaceLarge))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val activeTint = MaterialTheme.colorScheme.primary
                    val inactiveTint = MaterialTheme.colorScheme.onSurfaceVariant

                    IconButton(onClick = onShuffleClicked) {
                        Icon(
                            Icons.Filled.Shuffle,
                            contentDescription = stringResource(R.string.action_shuffle),
                            tint = if (isShuffleEnabled) activeTint else inactiveTint
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)
                    ) {
                        IconButton(onClick = onSkipPreviousClicked) {
                            Icon(
                                Icons.Filled.SkipPrevious,
                                contentDescription = stringResource(R.string.action_previous),
                                modifier = Modifier.size(Dimens.iconLarge)
                            )
                        }
                        IconButton(
                            onClick = onPlayPauseClicked,
                            modifier = Modifier.size(Dimens.iconPlayButton + Dimens.spaceLarge)
                        ) {
                            Icon(
                                if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = stringResource(
                                    if (isPlaying) R.string.action_pause else R.string.action_play
                                ),
                                modifier = Modifier.size(Dimens.iconPlayButton)
                            )
                        }
                        IconButton(onClick = onSkipNextClicked) {
                            Icon(
                                Icons.Filled.SkipNext,
                                contentDescription = stringResource(R.string.action_next),
                                modifier = Modifier.size(Dimens.iconLarge)
                            )
                        }
                    }

                    IconButton(onClick = onRepeatClicked) {
                        Icon(
                            if (repeatMode == RepeatMode.ONE) Icons.Filled.RepeatOne else Icons.Filled.Repeat,
                            contentDescription = stringResource(R.string.action_repeat),
                            tint = if (repeatMode != RepeatMode.OFF) activeTint else inactiveTint
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.spaceXXLarge))
            }
        }
    }
}

@Composable
private fun SeekBar(
    currentPositionMs: Long,
    durationMs: Long,
    onSeek: (Long) -> Unit
) {
    var isDragging by remember { mutableStateOf(false) }
    var dragProgress by remember { mutableFloatStateOf(0f) }

    val displayProgress = if (isDragging) {
        dragProgress
    } else if (durationMs > 0) {
        currentPositionMs.coerceIn(0L, durationMs).toFloat() / durationMs.toFloat()
    } else {
        0f
    }

    Slider(
        value = displayProgress,
        onValueChange = { newValue ->
            isDragging = true
            dragProgress = newValue
        },
        onValueChangeFinished = {
            val seekTarget = (dragProgress * durationMs).toLong()
            onSeek(seekTarget)
            isDragging = false
        },
        modifier = Modifier.fillMaxWidth()
    )
}

private fun formatDuration(ms: Long): String {
    val totalSeconds = TimeUnit.MILLISECONDS.toSeconds(ms.coerceAtLeast(0L))
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.US, "%d:%02d", minutes, seconds)
}
