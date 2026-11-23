package com.vyba.launcher.features.themes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vyba.launcher.VybaApplication
import com.vyba.launcher.data.models.CornerStyle
import com.vyba.launcher.data.models.IconShape
import com.vyba.launcher.data.models.LauncherPreferences
import com.vyba.launcher.data.models.ThemeMode
import com.vyba.launcher.data.models.VybaPreset
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ThemeStudioViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = (application as VybaApplication).preferencesRepository

    val preferences: StateFlow<LauncherPreferences> = prefs.preferencesFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = LauncherPreferences(),
    )

    fun selectPreset(preset: VybaPreset) {
        viewModelScope.launch { prefs.setPreset(preset) }
    }

    fun selectThemeMode(mode: ThemeMode) {
        viewModelScope.launch { prefs.setThemeMode(mode) }
    }

    fun setDynamicColor(enabled: Boolean) {
        viewModelScope.launch { prefs.setDynamicColor(enabled) }
    }

    fun selectCornerStyle(style: CornerStyle) {
        viewModelScope.launch { prefs.setCornerStyle(style) }
    }

    fun selectIconShape(shape: IconShape) {
        viewModelScope.launch { prefs.setIconShape(shape) }
    }

    fun setGridColumns(columns: Int) {
        viewModelScope.launch { prefs.setGridColumns(columns) }
    }

    fun setShowAppLabels(show: Boolean) {
        viewModelScope.launch { prefs.setShowAppLabels(show) }
    }
}
