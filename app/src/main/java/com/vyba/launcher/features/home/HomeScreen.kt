package com.vyba.launcher.features.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Wallpaper
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vyba.launcher.core.theme.LocalVybaThemeConfig
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

private enum class DrawerFilter(val label: String) {
    ALL("All"),
    USER("Downloaded"),
    PINNED("Pinned"),
    SYSTEM("System"),
}

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
    var activeFilter by remember { mutableStateOf(DrawerFilter.ALL) }
    val snackbarHostState = remember { SnackbarHostState() }
    var now by remember { mutableStateOf(LocalDateTime.now()) }
    val themeConfig = LocalVybaThemeConfig.current

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

    val scrimBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.background.copy(alpha = 0.42f),
            MaterialTheme.colorScheme.background.copy(alpha = 0.25f),
            MaterialTheme.colorScheme.background.copy(alpha = 0.68f),
        ),
    )

    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(scrimBrush)
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(innerPadding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = VybaSpacing.md),
            ) {
                Spacer(modifier = Modifier.height(VybaSpacing.md))

                // Editorial Clock & Quick Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Column {
                        Text(
                            text = now.format(timeFormatter),
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = 54.sp,
                                fontWeight = FontWeight.Light,
                                letterSpacing = (-2).sp,
                            ),
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        Text(
                            text = now.format(dateFormatter),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(VybaSpacing.sm))
                        Surface(
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable(onClick = onNavigateThemes),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.72f),
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.AutoAwesome,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp),
                                )
                                Text(
                                    text = if (themeConfig.useDynamicColor) "Material You" else themeConfig.preset.displayName,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                )
                            }
                        }
                    }

                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f),
                        modifier = Modifier.border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            shape = CircleShape,
                        ),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            IconButton(onClick = onNavigateWallpapers) {
                                Icon(
                                    imageVector = Icons.Rounded.Wallpaper,
                                    contentDescription = "Wallpapers",
                                    tint = MaterialTheme.colorScheme.onSurface,
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
                                    tint = MaterialTheme.colorScheme.onSurface,
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(VybaSpacing.lg))

                // Frosted Search Pill -> opens App Drawer
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.large)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                            shape = MaterialTheme.shapes.large,
                        )
                        .clickable { showDrawer = true },
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.78f),
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
                            text = "Search ${uiState.allApps.size} apps...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            text = "VYBA",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
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
                                    text = "Clean Home Canvas",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                Spacer(modifier = Modifier.height(VybaSpacing.xs))
                                Text(
                                    text = "Long-press any app in the drawer to pin it here or add it to your floating dock.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Spacer(modifier = Modifier.height(VybaSpacing.md))
                                VybaPrimaryButton(
                                    text = "Browse All Apps",
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

                // Floating Glass Dock
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = VybaSpacing.md)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.14f),
                            shape = MaterialTheme.shapes.extraLarge,
                        ),
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.84f),
                    tonalElevation = 6.dp,
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
                                onAppInfo = { onOpenAppInfo(dockApp.packageName) },
                                onUninstall = { onUninstallApp(dockApp.packageName) },
                                iconSize = 46.dp,
                                modifier = Modifier.weight(1f),
                            )
                        }
                        Surface(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .clickable { showDrawer = true },
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Rounded.Apps,
                                    contentDescription = "All Apps",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(24.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDrawer) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val filteredDrawerApps = remember(uiState.drawerApps, activeFilter, uiState.preferences.pinnedPackages) {
            when (activeFilter) {
                DrawerFilter.ALL -> uiState.drawerApps
                DrawerFilter.USER -> uiState.drawerApps.filter { !it.isSystemApp }
                DrawerFilter.PINNED -> uiState.drawerApps.filter { it.packageName in uiState.preferences.pinnedPackages }
                DrawerFilter.SYSTEM -> uiState.drawerApps.filter { it.isSystemApp }
            }
        }

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
                        placeholder = { Text("Search ${filteredDrawerApps.size} apps") },
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

                Spacer(modifier = Modifier.height(VybaSpacing.sm))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(VybaSpacing.xs),
                ) {
                    DrawerFilter.entries.forEach { filter ->
                        FilterChip(
                            selected = activeFilter == filter,
                            onClick = { activeFilter = filter },
                            label = { Text(filter.label) },
                        )
                    }
                }

                Spacer(modifier = Modifier.height(VybaSpacing.sm))

                if (filteredDrawerApps.isEmpty()) {
                    VybaEmptyState(
                        icon = Icons.Rounded.Apps,
                        title = "No matching apps",
                        description = "Try another filter, search term, or unhide apps in Settings.",
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(uiState.preferences.gridColumns),
                        verticalArrangement = Arrangement.spacedBy(VybaSpacing.sm),
                        horizontalArrangement = Arrangement.spacedBy(VybaSpacing.xs),
                        contentPadding = PaddingValues(bottom = VybaSpacing.xl),
                    ) {
                        items(filteredDrawerApps, key = { it.packageName }) { app ->
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
