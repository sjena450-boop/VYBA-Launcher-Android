package com.vyba.launcher.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vyba.launcher.VybaApplication
import com.vyba.launcher.features.home.HomeScreen
import com.vyba.launcher.features.home.HomeViewModel
import com.vyba.launcher.features.settings.SettingsScreen
import com.vyba.launcher.features.themes.ThemeStudioScreen
import com.vyba.launcher.features.themes.ThemeStudioViewModel
import com.vyba.launcher.features.wallpapers.WallpapersScreen
import com.vyba.launcher.features.wallpapers.WallpapersViewModel
import kotlinx.coroutines.launch

object VybaRoutes {
    const val HOME = "home"
    const val THEME_STUDIO = "theme_studio"
    const val WALLPAPERS = "wallpapers"
    const val SETTINGS = "settings"
}

@Composable
fun VybaNavGraph() {
    val navController = rememberNavController()
    val homeViewModel: HomeViewModel = viewModel()
    val themeViewModel: ThemeStudioViewModel = viewModel()
    val wallpapersViewModel: WallpapersViewModel = viewModel()

    val homeState by homeViewModel.uiState.collectAsState()
    val preferences by themeViewModel.preferences.collectAsState()
    val wallpaperState by wallpapersViewModel.uiState.collectAsState()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = VybaRoutes.HOME,
    ) {
        composable(VybaRoutes.HOME) {
            HomeScreen(
                uiState = homeState,
                onSearchChange = homeViewModel::updateSearchQuery,
                onLaunchApp = homeViewModel::launchApp,
                onTogglePin = homeViewModel::togglePinned,
                onMovePinned = homeViewModel::movePinned,
                onToggleDock = homeViewModel::toggleDock,
                onToggleHide = homeViewModel::toggleHidden,
                onOpenAppInfo = homeViewModel::openAppInfo,
                onUninstallApp = homeViewModel::uninstallApp,
                onRefreshApps = homeViewModel::refreshApps,
                onClearStatus = homeViewModel::clearStatusMessage,
                onNavigateThemes = { navController.navigate(VybaRoutes.THEME_STUDIO) },
                onNavigateWallpapers = { navController.navigate(VybaRoutes.WALLPAPERS) },
                onNavigateSettings = { navController.navigate(VybaRoutes.SETTINGS) },
            )
        }

        composable(VybaRoutes.THEME_STUDIO) {
            ThemeStudioScreen(
                preferences = preferences,
                onSelectPreset = themeViewModel::selectPreset,
                onSelectMode = themeViewModel::selectThemeMode,
                onToggleDynamicColor = themeViewModel::setDynamicColor,
                onSelectCornerStyle = themeViewModel::selectCornerStyle,
                onSelectIconShape = themeViewModel::selectIconShape,
                onSelectGridColumns = themeViewModel::setGridColumns,
                onToggleLabels = themeViewModel::setShowAppLabels,
                onBack = { navController.popBackStack() },
            )
        }

        composable(VybaRoutes.WALLPAPERS) {
            WallpapersScreen(
                uiState = wallpaperState,
                onSelectPreset = wallpapersViewModel::selectPreset,
                onApplyWallpaper = wallpapersViewModel::applySelected,
                onClearMessage = wallpapersViewModel::clearMessage,
                onBack = { navController.popBackStack() },
            )
        }

        composable(VybaRoutes.SETTINGS) {
            SettingsScreen(
                hiddenApps = homeState.hiddenApps,
                onUnhideApp = homeViewModel::toggleHidden,
                onResetPreferences = {
                    scope.launch {
                        (context.applicationContext as VybaApplication).preferencesRepository.resetAll()
                    }
                },
                onOpenThemeStudio = { navController.navigate(VybaRoutes.THEME_STUDIO) },
                onBack = { navController.popBackStack() },
            )
        }
    }
}
