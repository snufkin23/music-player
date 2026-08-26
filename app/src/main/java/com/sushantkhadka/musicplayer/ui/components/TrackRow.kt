package com.sushantkhadka.musicplayer.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sushantkhadka.musicplayer.ui.theme.AppTextStyles
import com.sushantkhadka.musicplayer.ui.theme.Dimens

@Composable
fun TrackRow(
    title: String,
    subtitle: String? = null,
    trackNumber: Int? = null,
    isCurrentlyPlaying: Boolean = false,
    onClick: () -> Unit
) {
    val background = if (isCurrentlyPlaying) {
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
        if (trackNumber != null) {
            Row(
                modifier = Modifier.padding(
                    horizontal = Dimens.spaceLarge,
                    vertical = Dimens.spaceMedium
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = trackNumber.toString(),
                    style = AppTextStyles.caption,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(end = Dimens.spaceMedium)
                )
                Text(text = title, style = AppTextStyles.itemTitle)
            }
        } else {
            Column(
                modifier = Modifier.padding(
                    horizontal = Dimens.spaceLarge,
                    vertical = Dimens.spaceMedium
                )
            ) {
                Text(text = title, style = AppTextStyles.itemTitle)
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = AppTextStyles.caption,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
