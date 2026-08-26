package com.sushantkhadka.musicplayer.ui.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sushantkhadka.musicplayer.domain.model.MusicFolder
import com.sushantkhadka.musicplayer.domain.repository.FolderRepository
import com.sushantkhadka.musicplayer.domain.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val folderRepository: FolderRepository,
    private val musicRepository: MusicRepository
) : ViewModel() {

    val folders: StateFlow<List<MusicFolder>?> = folderRepository.observeFolders()
        .map { it as List<MusicFolder>? }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun addFolder(uri: Uri) {
        viewModelScope.launch {
            folderRepository.addFolder(uri)
            musicRepository.rescanLibrary()
        }
    }

    fun removeFolder(folder: MusicFolder) {
        viewModelScope.launch {
            folderRepository.removeFolder(folder)
            musicRepository.rescanLibrary()
        }
    }

    fun rescanLibrary() {
        viewModelScope.launch { musicRepository.rescanLibrary() }
    }
}
