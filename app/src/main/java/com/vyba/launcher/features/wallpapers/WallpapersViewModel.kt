package com.vyba.launcher.features.wallpapers

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vyba.launcher.VybaApplication
import com.vyba.launcher.data.models.VybaWallpapers
import com.vyba.launcher.data.models.WallpaperPreset
import com.vyba.launcher.data.repositories.WallpaperTarget
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WallpapersUiState(
    val presets: List<WallpaperPreset> = VybaWallpapers.presets,
    val selectedPreset: WallpaperPreset = VybaWallpapers.presets.first(),
    val isApplying: Boolean = false,
    val message: String? = null,
)

class WallpapersViewModel(application: Application) : AndroidViewModel(application) {
    private val applier = (application as VybaApplication).wallpaperApplier

    private val _uiState = MutableStateFlow(WallpapersUiState())
    val uiState: StateFlow<WallpapersUiState> = _uiState.asStateFlow()

    fun selectPreset(preset: WallpaperPreset) {
        _uiState.value = _uiState.value.copy(selectedPreset = preset)
    }

    fun applySelected(target: WallpaperTarget) {
        val current = _uiState.value.selectedPreset
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isApplying = true)
            val result = applier.applyWallpaper(current, target)
            _uiState.value = _uiState.value.copy(
                isApplying = false,
                message = if (result.isSuccess) {
                    "Applied ${current.title} to ${target.label}."
                } else {
                    "Failed to apply wallpaper: ${result.exceptionOrNull()?.localizedMessage ?: "Unknown error"}"
                },
            )
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
}
