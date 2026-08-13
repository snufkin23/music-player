package com.sushantkhadka.musicplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sushantkhadka.musicplayer.permission.PermissionGate
import com.sushantkhadka.musicplayer.ui.theme.MusicplayerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			MusicplayerTheme {
				PermissionGate {
					com.sushantkhadka.musicplayer.ui.library.LibraryScreen()
				}
			}
		}
	}
}