package com.sushantkhadka.musicplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sushantkhadka.musicplayer.ui.setup.FolderGate
import com.sushantkhadka.musicplayer.ui.theme.MusicplayerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			MusicplayerTheme {
				FolderGate {
					com.sushantkhadka.musicplayer.ui.navigation.AppRoot()
				}
			}
		}
	}
}