package com.vyba.launcher.features.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vyba.launcher.VybaApplication
import com.vyba.launcher.data.models.AppInfo
import com.vyba.launcher.data.models.LauncherPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val allApps: List<AppInfo> = emptyList(),
    val drawerApps: List<AppInfo> = emptyList(),
    val pinnedApps: List<AppInfo> = emptyList(),
    val dockApps: List<AppInfo> = emptyList(),
    val hiddenApps: List<AppInfo> = emptyList(),
    val preferences: LauncherPreferences = LauncherPreferences(),
    val statusMessage: String? = null,
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as VybaApplication
    private val appRepository = app.appRepository
    private val prefsRepository = app.preferencesRepository

    private val rawApps = MutableStateFlow<List<AppInfo>>(emptyList())
    private val isLoading = MutableStateFlow(true)
    private val searchQuery = MutableStateFlow("")
    private val statusMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<HomeUiState> = combine(
        isLoading,
        searchQuery,
        rawApps,
        prefsRepository.preferencesFlow,
        statusMessage,
    ) { loading, query, apps, prefs, message ->
        val visibleApps = filterVisibleApps(apps, query, prefs.hiddenPackages)
        val pinned = apps.filter { it.packageName in prefs.pinnedPackages && it.packageName !in prefs.hiddenPackages }
        val dock = prefs.dockPackages.mapNotNull { pkg -> apps.firstOrNull { it.packageName == pkg } }
        val hidden = apps.filter { it.packageName in prefs.hiddenPackages }

        HomeUiState(
            isLoading = loading,
            searchQuery = query,
            allApps = apps,
            drawerApps = visibleApps,
            pinnedApps = pinned,
            dockApps = dock,
            hiddenApps = hidden,
            preferences = prefs,
            statusMessage = message,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState(),
    )

    init {
        refreshApps()
    }

    fun refreshApps() {
        viewModelScope.launch {
            isLoading.value = true
            rawApps.value = appRepository.loadLaunchableApps()
            isLoading.value = false
        }
    }

    fun updateSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun launchApp(packageName: String) {
        appRepository.launchApp(packageName).onFailure {
            statusMessage.value = "Unable to launch app."
        }
    }

    fun openAppInfo(packageName: String) {
        appRepository.openAppInfo(packageName).onFailure {
            statusMessage.value = "Unable to open app details."
        }
    }

    fun uninstallApp(packageName: String) {
        appRepository.uninstallApp(packageName).onFailure {
            statusMessage.value = "Unable to start uninstall."
        }
    }

    fun togglePinned(packageName: String) {
        viewModelScope.launch { prefsRepository.togglePinnedPackage(packageName) }
    }

    fun toggleDock(packageName: String) {
        viewModelScope.launch { prefsRepository.toggleDockPackage(packageName) }
    }

    fun toggleHidden(packageName: String) {
        viewModelScope.launch { prefsRepository.toggleHiddenPackage(packageName) }
    }

    fun clearStatusMessage() {
        statusMessage.value = null
    }

    companion object {
        fun filterVisibleApps(
            apps: List<AppInfo>,
            query: String,
            hiddenPackages: Set<String>,
        ): List<AppInfo> {
            val trimmed = query.trim().lowercase()
            return apps.filter { app ->
                app.packageName !in hiddenPackages &&
                    (trimmed.isEmpty() || app.label.lowercase().contains(trimmed))
            }
        }
    }
}
