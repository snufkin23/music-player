package com.sushantkhadka.musicplayer.ui.queue

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import coil3.compose.AsyncImage
import com.sushantkhadka.musicplayer.R
import com.sushantkhadka.musicplayer.data.imageloading.AlbumArtRequest
import com.sushantkhadka.musicplayer.ui.player.QueueItemUi
import com.sushantkhadka.musicplayer.ui.theme.AppTextStyles
import com.sushantkhadka.musicplayer.ui.theme.Dimens

@Composable
fun QueueScreen(
    items: List<QueueItemUi>,
    currentIndex: Int,
    onItemClicked: (Int) -> Unit,
    onRemove: (Int) -> Unit,
    onMove: (Int, Int) -> Unit,
    onBack: () -> Unit
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.action_back))
                }
                Text(stringResource(R.string.queue_title), style = AppTextStyles.headerTitle)
            }

            if (items.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.queue_empty))
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    itemsIndexed(items, key = { _, item -> item.mediaId }) { index, item ->
                        QueueRow(
                            item = item,
                            isCurrent = index == currentIndex,
                            canMoveUp = index > 0,
                            canMoveDown = index < items.lastIndex,
                            onClick = { onItemClicked(index) },
                            onMoveUp = { onMove(index, index - 1) },
                            onMoveDown = { onMove(index, index + 1) },
                            onRemove = { onRemove(index) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QueueRow(
    item: QueueItemUi,
    isCurrent: Boolean,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onClick: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onRemove: () -> Unit
) {
    val background = if (isCurrent) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = background
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Dimens.spaceMedium, vertical = Dimens.spaceSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = AlbumArtRequest(Uri.parse(item.mediaId)),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                error = ColorPainter(MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier
                    .size(Dimens.albumArtQueue)
                    .clip(RoundedCornerShape(Dimens.cornerRadiusSmall))
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = Dimens.spaceXS)
            ) {
                Text(
                    text = item.title,
                    style = AppTextStyles.compactTitle,
                    maxLines = 1
                )
                Text(
                    text = item.artist,
                    style = AppTextStyles.caption,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            IconButton(onClick = onMoveUp, enabled = canMoveUp) {
                Icon(
                    Icons.Filled.KeyboardArrowUp,
                    contentDescription = stringResource(R.string.action_move_up)
                )
            }
            IconButton(onClick = onMoveDown, enabled = canMoveDown) {
                Icon(
                    Icons.Filled.KeyboardArrowDown,
                    contentDescription = stringResource(R.string.action_move_down)
                )
            }
            IconButton(onClick = onRemove) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = stringResource(R.string.action_remove_from_queue)
                )
            }
        }
    }
}
