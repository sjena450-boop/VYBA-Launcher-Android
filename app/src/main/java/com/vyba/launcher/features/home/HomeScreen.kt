package com.vyba.launcher.features.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Wallpaper
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vyba.launcher.core.theme.VybaSpacing
import com.vyba.launcher.core.theme.components.VybaCard
import com.vyba.launcher.core.theme.components.VybaEmptyState
import com.vyba.launcher.core.theme.components.VybaLoadingState
import com.vyba.launcher.core.theme.components.VybaPrimaryButton
import com.vyba.launcher.ui.components.AppIconTile
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onSearchChange: (String) -> Unit,
    onLaunchApp: (String) -> Unit,
    onTogglePin: (String) -> Unit,
    onToggleDock: (String) -> Unit,
    onToggleHide: (String) -> Unit,
    onOpenAppInfo: (String) -> Unit,
    onUninstallApp: (String) -> Unit,
    onRefreshApps: () -> Unit,
    onClearStatus: () -> Unit,
    onNavigateThemes: () -> Unit,
    onNavigateWallpapers: () -> Unit,
    onNavigateSettings: () -> Unit,
) {
    var showDrawer by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    var now by remember { mutableStateOf(LocalDateTime.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            now = LocalDateTime.now()
            delay(30_000L)
        }
    }

    LaunchedEffect(uiState.statusMessage) {
        uiState.statusMessage?.let {
            snackbarHostState.showSnackbar(it)
            onClearStatus()
        }
    }

    BackHandler(enabled = showDrawer) {
        showDrawer = false
    }

    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault()) }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("EEEE, MMM d", Locale.getDefault()) }

    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.55f))
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(innerPadding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = VybaSpacing.md),
            ) {
                Spacer(modifier = Modifier.height(VybaSpacing.lg))

                // Clock & Date Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Column {
                        Text(
                            text = now.format(timeFormatter),
                            style = MaterialTheme.typography.displayLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        Text(
                            text = now.format(dateFormatter),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(VybaSpacing.xs)) {
                        IconButton(onClick = onNavigateWallpapers) {
                            Icon(
                                imageVector = Icons.Rounded.Wallpaper,
                                contentDescription = "Wallpapers",
                                tint = MaterialTheme.colorScheme.onBackground,
                            )
                        }
                        IconButton(onClick = onNavigateThemes) {
                            Icon(
                                imageVector = Icons.Rounded.Palette,
                                contentDescription = "Theme Studio",
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                        IconButton(onClick = onNavigateSettings) {
                            Icon(
                                imageVector = Icons.Rounded.Settings,
                                contentDescription = "Settings",
                                tint = MaterialTheme.colorScheme.onBackground,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(VybaSpacing.lg))

                // Search Pill -> opens App Drawer
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.large)
                        .clickable { showDrawer = true },
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                    tonalElevation = 2.dp,
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = VybaSpacing.md, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(modifier = Modifier.width(VybaSpacing.sm))
                        Text(
                            text = "Search apps or open drawer...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(VybaSpacing.lg))

                // Pinned Home Apps Grid
                Box(modifier = Modifier.weight(1f)) {
                    when {
                        uiState.isLoading -> VybaLoadingState(message = "Loading installed apps...")
                        uiState.pinnedApps.isEmpty() -> {
                            VybaCard(modifier = Modifier.align(Alignment.TopCenter)) {
                                Text(
                                    text = "Your VYBA Home Grid",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                Spacer(modifier = Modifier.height(VybaSpacing.xs))
                                Text(
                                    text = "Long-press any app in the drawer to pin it here or add it to your dock.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Spacer(modifier = Modifier.height(VybaSpacing.md))
                                VybaPrimaryButton(
                                    text = "Open App Drawer",
                                    onClick = { showDrawer = true },
                                )
                            }
                        }
                        else -> {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(uiState.preferences.gridColumns),
                                verticalArrangement = Arrangement.spacedBy(VybaSpacing.sm),
                                horizontalArrangement = Arrangement.spacedBy(VybaSpacing.xs),
                                contentPadding = PaddingValues(bottom = VybaSpacing.md),
                            ) {
                                items(uiState.pinnedApps, key = { it.packageName }) { app ->
                                    AppIconTile(
                                        app = app,
                                        showLabel = uiState.preferences.showAppLabels,
                                        isPinned = true,
                                        isDocked = app.packageName in uiState.preferences.dockPackages,
                                        onLaunch = { onLaunchApp(app.packageName) },
                                        onTogglePin = { onTogglePin(app.packageName) },
                                        onToggleDock = { onToggleDock(app.packageName) },
                                        onHideApp = { onToggleHide(app.packageName) },
                                        onAppInfo = { onOpenAppInfo(app.packageName) },
                                        onUninstall = { onUninstallApp(app.packageName) },
                                    )
                                }
                            }
                        }
                    }
                }

                // Bottom Dock
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = VybaSpacing.md),
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
                    tonalElevation = 4.dp,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = VybaSpacing.sm, vertical = VybaSpacing.xs),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        uiState.dockApps.forEach { dockApp ->
                            AppIconTile(
                                app = dockApp,
                                showLabel = false,
                                isPinned = dockApp.packageName in uiState.preferences.pinnedPackages,
                                isDocked = true,
                                onLaunch = { onLaunchApp(dockApp.packageName) },
                                onTogglePin = { onTogglePin(dockApp.packageName) },
                                onToggleDock = { onToggleDock(dockApp.packageName) },
                                onHideApp = { onToggleHide(dockApp.packageName) },
                                onOpenAppInfo = { onOpenAppInfo(dockApp.packageName) },
                                onUninstall = { onUninstallApp(dockApp.packageName) },
                                iconSize = 46.dp,
                                modifier = Modifier.weight(1f),
                            )
                        }
                        IconButton(
                            onClick = { showDrawer = true },
                            modifier = Modifier.size(52.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Apps,
                                contentDescription = "All Apps",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp),
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDrawer) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showDrawer = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = VybaSpacing.md),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = onSearchChange,
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = MaterialTheme.shapes.large,
                        placeholder = { Text("Search ${uiState.drawerApps.size} apps") },
                        leadingIcon = {
                            Icon(Icons.Rounded.Search, contentDescription = null)
                        },
                        trailingIcon = {
                            if (uiState.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchChange("") }) {
                                    Icon(Icons.Rounded.Close, contentDescription = "Clear search")
                                }
                            }
                        },
                    )
                    Spacer(modifier = Modifier.width(VybaSpacing.xs))
                    IconButton(onClick = onRefreshApps) {
                        Icon(Icons.Rounded.Refresh, contentDescription = "Refresh apps")
                    }
                }

                Spacer(modifier = Modifier.height(VybaSpacing.md))

                if (uiState.drawerApps.isEmpty()) {
                    VybaEmptyState(
                        icon = Icons.Rounded.Apps,
                        title = "No matching apps",
                        description = "Try a different search term or unhide apps in Settings.",
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(uiState.preferences.gridColumns),
                        verticalArrangement = Arrangement.spacedBy(VybaSpacing.sm),
                        horizontalArrangement = Arrangement.spacedBy(VybaSpacing.xs),
                        contentPadding = PaddingValues(bottom = VybaSpacing.xl),
                    ) {
                        items(uiState.drawerApps, key = { it.packageName }) { app ->
                            AppIconTile(
                                app = app,
                                showLabel = uiState.preferences.showAppLabels,
                                isPinned = app.packageName in uiState.preferences.pinnedPackages,
                                isDocked = app.packageName in uiState.preferences.dockPackages,
                                onLaunch = {
                                    showDrawer = false
                                    onLaunchApp(app.packageName)
                                },
                                onTogglePin = { onTogglePin(app.packageName) },
                                onToggleDock = { onToggleDock(app.packageName) },
                                onHideApp = { onToggleHide(app.packageName) },
                                onAppInfo = { onOpenAppInfo(app.packageName) },
                                onUninstall = { onUninstallApp(app.packageName) },
                            )
                        }
                    }
                }
            }
        }
    }
}
