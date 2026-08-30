package com.sushantkhadka.musicplayer.ui.navigation

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil3.compose.AsyncImage
import com.sushantkhadka.musicplayer.R
import com.sushantkhadka.musicplayer.data.imageloading.AlbumArtRequest
import com.sushantkhadka.musicplayer.ui.albumdetail.AlbumDetailScreen
import com.sushantkhadka.musicplayer.ui.albums.AlbumsScreen
import com.sushantkhadka.musicplayer.ui.artistdetail.ArtistDetailScreen
import com.sushantkhadka.musicplayer.ui.artists.ArtistsScreen
import com.sushantkhadka.musicplayer.ui.library.LibraryScreen
import com.sushantkhadka.musicplayer.ui.nowplaying.NowPlayingScreen
import com.sushantkhadka.musicplayer.ui.player.PlayerViewModel
import com.sushantkhadka.musicplayer.ui.queue.QueueScreen
import com.sushantkhadka.musicplayer.ui.search.SearchScreen
import com.sushantkhadka.musicplayer.ui.settings.SettingsScreen
import com.sushantkhadka.musicplayer.ui.theme.AppTextStyles
import com.sushantkhadka.musicplayer.ui.theme.Dimens

private object Routes {
    const val LIBRARY = "library"
    const val ALBUMS = "albums"
    const val ARTISTS = "artists"
    const val SEARCH = "search"
    const val ALBUM_DETAIL = "album/{albumKey}"
    const val ARTIST_DETAIL = "artist/{artistKey}"
    const val NOW_PLAYING = "nowplaying"
    const val QUEUE = "queue"
    const val SETTINGS = "settings"

    fun albumDetail(albumKey: String) = "album/${Uri.encode(albumKey)}"
    fun artistDetail(artistKey: String) = "artist/${Uri.encode(artistKey)}"
}

@Composable
fun AppRoot(
    playerViewModel: PlayerViewModel = hiltViewModel()
) {
    val playbackState by playerViewModel.playbackState.collectAsState()
    val currentPositionMs by playerViewModel.currentPositionMs.collectAsState()
    val queueItems by playerViewModel.queueItems.collectAsState()
    val currentQueueIndex by playerViewModel.currentQueueIndex.collectAsState()

    val navController = rememberNavController()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val isFullScreen = currentRoute == Routes.NOW_PLAYING || currentRoute == Routes.QUEUE

    Scaffold(
        bottomBar = {
            if (!isFullScreen) {
                Column {
                    MiniPlayerBar(
                        title = playbackState.title,
                        artist = playbackState.artist,
                        artUri = playbackState.currentMediaId,
                        isPlaying = playbackState.isPlaying,
                        isConnected = playbackState.isConnected,
                        currentPositionMs = currentPositionMs,
                        durationMs = playbackState.durationMs,
                        onBarClicked = {
                            navController.navigate(Routes.NOW_PLAYING) { launchSingleTop = true }
                        },
                        onPlayPauseClicked = playerViewModel::onPlayPauseClicked,
                        onSkipNextClicked = playerViewModel::onSkipNextClicked,
                        onSkipPreviousClicked = playerViewModel::onSkipPreviousClicked
                    )
                    BottomNavBar(navController)
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.LIBRARY,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.LIBRARY) {
                LibraryScreen(
                    currentMediaId = playbackState.currentMediaId,
                    onSettingsClicked = { navController.navigate(Routes.SETTINGS) }
                )
            }
            composable(Routes.ALBUMS) {
                AlbumsScreen(
                    onAlbumClicked = { albumKey ->
                        navController.navigate(Routes.albumDetail(albumKey))
                    }
                )
            }
            composable(Routes.ARTISTS) {
                ArtistsScreen(
                    onArtistClicked = { artistKey ->
                        navController.navigate(Routes.artistDetail(artistKey))
                    }
                )
            }
            composable(Routes.SEARCH) {
                SearchScreen(currentMediaId = playbackState.currentMediaId)
            }
            composable(
                route = Routes.ALBUM_DETAIL,
                arguments = listOf(navArgument("albumKey") { type = NavType.StringType })
            ) {
                AlbumDetailScreen(
                    currentMediaId = playbackState.currentMediaId,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Routes.ARTIST_DETAIL,
                arguments = listOf(navArgument("artistKey") { type = NavType.StringType })
            ) {
                ArtistDetailScreen(
                    currentMediaId = playbackState.currentMediaId,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.NOW_PLAYING) {
                NowPlayingScreen(
                    title = playbackState.title,
                    artist = playbackState.artist,
                    artUri = playbackState.currentMediaId,
                    isPlaying = playbackState.isPlaying,
                    isShuffleEnabled = playbackState.isShuffleEnabled,
                    repeatMode = playbackState.repeatMode,
                    currentPositionMs = currentPositionMs,
                    durationMs = playbackState.durationMs,
                    onPlayPauseClicked = playerViewModel::onPlayPauseClicked,
                    onSkipNextClicked = playerViewModel::onSkipNextClicked,
                    onSkipPreviousClicked = playerViewModel::onSkipPreviousClicked,
                    onShuffleClicked = playerViewModel::onShuffleClicked,
                    onRepeatClicked = playerViewModel::onRepeatClicked,
                    onSeek = playerViewModel::onSeek,
                    onQueueClicked = {
                        navController.navigate(Routes.QUEUE) { launchSingleTop = true }
                    },
                    onCollapse = { navController.popBackStack() }
                )
            }
            composable(Routes.QUEUE) {
                QueueScreen(
                    items = queueItems,
                    currentIndex = currentQueueIndex,
                    onItemClicked = playerViewModel::onQueueItemClicked,
                    onRemove = playerViewModel::onRemoveFromQueue,
                    onMove = playerViewModel::onMoveQueueItem,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.SETTINGS) {
                SettingsScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}

@Composable
private fun BottomNavBar(navController: androidx.navigation.NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        NavigationBarItem(
            selected = currentDestination?.hierarchy?.any { it.route == Routes.LIBRARY } == true,
            onClick = {
                navController.navigate(Routes.LIBRARY) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Icon(Icons.Filled.LibraryMusic, contentDescription = stringResource(R.string.nav_library)) },
            label = { Text(stringResource(R.string.nav_library)) }
        )
        NavigationBarItem(
            selected = currentDestination?.hierarchy?.any { it.route == Routes.ALBUMS } == true,
            onClick = {
                navController.navigate(Routes.ALBUMS) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Icon(Icons.Filled.Album, contentDescription = stringResource(R.string.nav_albums)) },
            label = { Text(stringResource(R.string.nav_albums)) }
        )
        NavigationBarItem(
            selected = currentDestination?.hierarchy?.any { it.route == Routes.ARTISTS } == true,
            onClick = {
                navController.navigate(Routes.ARTISTS) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Icon(Icons.Filled.Person, contentDescription = stringResource(R.string.nav_artists)) },
            label = { Text(stringResource(R.string.nav_artists)) }
        )
        NavigationBarItem(
            selected = currentDestination?.hierarchy?.any { it.route == Routes.SEARCH } == true,
            onClick = {
                navController.navigate(Routes.SEARCH) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Icon(Icons.Filled.Search, contentDescription = stringResource(R.string.nav_search)) },
            label = { Text(stringResource(R.string.nav_search)) }
        )
    }
}

@Composable
private fun MiniPlayerBar(
    title: String,
    artist: String,
    artUri: String?,
    isPlaying: Boolean,
    isConnected: Boolean,
    currentPositionMs: Long,
    durationMs: Long,
    onBarClicked: () -> Unit,
    onPlayPauseClicked: () -> Unit,
    onSkipNextClicked: () -> Unit,
    onSkipPreviousClicked: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onBarClicked),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column {
            val progress = if (durationMs > 0) {
                (currentPositionMs.coerceIn(0L, durationMs).toFloat() / durationMs.toFloat())
            } else {
                0f
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.spaceMedium, vertical = Dimens.spaceSmall),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AsyncImage(
                    model = artUri?.let { AlbumArtRequest(Uri.parse(it)) },
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    error = ColorPainter(MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .size(Dimens.albumArtThumbnail)
                        .clip(RoundedCornerShape(Dimens.spaceXSmall * 1.5f))
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = Dimens.spaceSmall)
                ) {
                    Text(
                        text = title.ifEmpty {
                            if (isConnected) stringResource(R.string.nothing_playing) else stringResource(R.string.connecting)
                        },
                        style = AppTextStyles.compactTitle
                    )
                    if (artist.isNotEmpty()) {
                        Text(text = artist, style = AppTextStyles.caption)
                    }
                }

                IconButton(onClick = onSkipPreviousClicked) {
                    Icon(Icons.Filled.SkipPrevious, contentDescription = stringResource(R.string.action_previous))
                }
                IconButton(onClick = onPlayPauseClicked) {
                    Icon(
                        if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = stringResource(
                            if (isPlaying) R.string.action_pause else R.string.action_play
                        )
                    )
                }
                IconButton(onClick = onSkipNextClicked) {
                    Icon(Icons.Filled.SkipNext, contentDescription = stringResource(R.string.action_next))
                }
            }
        }
    }
}
