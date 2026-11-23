package com.vyba.launcher.features.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Wallpaper
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextButton
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vyba.launcher.core.theme.VybaSpacing
import com.vyba.launcher.core.theme.components.VybaCard
import com.vyba.launcher.core.theme.components.VybaEmptyState
import com.vyba.launcher.core.theme.components.VybaLoadingState
import com.vyba.launcher.core.theme.components.VybaPrimaryButton
import com.vyba.launcher.ui.components.AppIconTile
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
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
    onMovePinned: (String, Int) -> Unit,
    onCreateHomeFolder: (String, List<String>) -> Unit,
    onDeleteHomeFolder: (String) -> Unit,
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
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Refresh the launcher inventory whenever the user returns to VYBA.
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) onRefreshApps()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Keep the app drawer in sync with package installs, removals and updates.
    DisposableEffect(context) {
        val packageReceiver = object : BroadcastReceiver() {
            override fun onReceive(receiverContext: Context?, intent: Intent?) {
                val changedPackage = intent?.data?.schemeSpecificPart
                if (changedPackage != context.packageName) onRefreshApps()
            }
        }
        val packageFilter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_CHANGED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addDataScheme("package")
        }
        ContextCompat.registerReceiver(
            context,
            packageReceiver,
            packageFilter,
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )
        onDispose { context.unregisterReceiver(packageReceiver) }
    }

    var showDrawer by remember { mutableStateOf(false) }
    var isHomeEditMode by remember { mutableStateOf(false) }
    var selectedHomePage by remember { mutableStateOf(0) }
    var selectedForFolder by remember { mutableStateOf<Set<String>>(emptySet()) }
    var folderName by remember { mutableStateOf("") }
    var showCreateFolderDialog by remember { mutableStateOf(false) }
    var selectedFolderId by remember { mutableStateOf<String?>(null) }
    val homePages = uiState.pinnedApps.chunked(12)
    val safeHomePage = selectedHomePage.coerceIn(0, (homePages.size - 1).coerceAtLeast(0))
    val currentHomeApps = homePages.getOrNull(safeHomePage).orEmpty()
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(if (isHomeEditMode) "Edit mode" else "Home", style = MaterialTheme.typography.labelLarge)
                    IconButton(onClick = { isHomeEditMode = !isHomeEditMode }) {
                        Icon(if (isHomeEditMode) Icons.Rounded.Check else Icons.Rounded.Edit, contentDescription = "Toggle home edit mode")
                    }
                }

                if (isHomeEditMode) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "${selectedForFolder.size} apps selected", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        TextButton(enabled = selectedForFolder.size >= 2, onClick = { showCreateFolderDialog = true }) { Text("Create folder") }
                        TextButton(onClick = { selectedForFolder = emptySet() }, enabled = selectedForFolder.isNotEmpty()) { Text("Clear") }
                    }
                }
                if (uiState.preferences.homeFolders.isNotEmpty()) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(VybaSpacing.xs)) {
                        uiState.preferences.homeFolders.forEach { folder ->
                            Surface(modifier = Modifier.clickable { selectedFolderId = folder.id }, shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.secondaryContainer) {
                                Text(text = "${folder.name} · ${folder.packageNames.size}", modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSecondaryContainer)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(VybaSpacing.xs))
                }
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
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .pointerInput(homePages.size, safeHomePage) {
                            var horizontalDrag = 0f
                            detectHorizontalDragGestures(
                                onHorizontalDrag = { change, dragAmount ->
                                    horizontalDrag += dragAmount
                                    change.consume()
                                },
                                onDragEnd = {
                                    when {
                                        horizontalDrag <= -60f -> selectedHomePage = (safeHomePage + 1).coerceAtMost(homePages.lastIndex)
                                        horizontalDrag >= 60f -> selectedHomePage = (safeHomePage - 1).coerceAtLeast(0)
                                    }
                                    horizontalDrag = 0f
                                },
                                onDragCancel = { horizontalDrag = 0f },
                            )
                        },
                ) {
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
                                items(currentHomeApps, key = { it.packageName }) { app ->
                                    val localIndex = currentHomeApps.indexOfFirst { it.packageName == app.packageName }
                                    val pageStartIndex = safeHomePage * 12
                                    val absoluteIndex = pageStartIndex + localIndex
                                    AppIconTile(
                                        modifier = (if (app.packageName in selectedForFolder) {
                                            Modifier.background(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.medium)
                                        } else Modifier).then(if (isHomeEditMode) {
                                            Modifier.pointerInput(app.packageName, safeHomePage, uiState.preferences.gridColumns) {
                                                var dragX = 0f
                                                var dragY = 0f
                                                detectDragGestures(
                                                    onDrag = { change, amount ->
                                                        change.consume()
                                                        dragX += amount.x
                                                        dragY += amount.y
                                                    },
                                                    onDragEnd = {
                                                        val threshold = 36f
                                                        val columns = uiState.preferences.gridColumns
                                                        val step = if (kotlin.math.abs(dragX) > kotlin.math.abs(dragY)) {
                                                            if (dragX > threshold) 1 else if (dragX < -threshold) -1 else 0
                                                        } else {
                                                            if (dragY > threshold) columns else if (dragY < -threshold) -columns else 0
                                                        }
                                                        if (step != 0) {
                                                            val pageEndIndex = (pageStartIndex + currentHomeApps.size - 1)
                                                            val target = (absoluteIndex + step).coerceIn(pageStartIndex, pageEndIndex)
                                                            if (target != absoluteIndex) onMovePinned(app.packageName, target)
                                                        }
                                                        dragX = 0f
                                                        dragY = 0f
                                                    },
                                                    onDragCancel = {
                                                        dragX = 0f
                                                        dragY = 0f
                                                    },
                                                )
                                            }
                                        } else Modifier),
                                        app = app,
                                        showLabel = uiState.preferences.showAppLabels,
                                        isPinned = true,
                                        isDocked = app.packageName in uiState.preferences.dockPackages,
                                        onLaunch = {
                                            if (isHomeEditMode) {
                                                selectedForFolder = if (app.packageName in selectedForFolder) selectedForFolder - app.packageName else selectedForFolder + app.packageName
                                            } else onLaunchApp(app.packageName)
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
                    if (homePages.size > 1) {
                        Row(
                            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = VybaSpacing.xs),
                            horizontalArrangement = Arrangement.spacedBy(VybaSpacing.sm),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            homePages.indices.forEach { page ->
                                Surface(
                                    modifier = Modifier.size(if (page == safeHomePage) 10.dp else 7.dp)
                                        .clip(MaterialTheme.shapes.small).clickable { selectedHomePage = page },
                                    color = if (page == safeHomePage) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
                                ) {}
                            }
                        }
                    }
                    if (isHomeEditMode) {
                        Text(
                            text = "Drag apps to rearrange · ${uiState.pinnedApps.size} pinned apps",
                            modifier = Modifier.align(Alignment.TopCenter),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
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
                                onAppInfo = { onOpenAppInfo(dockApp.packageName) },
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

    if (showCreateFolderDialog) {
        AlertDialog(
            onDismissRequest = { showCreateFolderDialog = false },
            title = { Text("Create home folder") },
            text = { Column { Text("Add the ${selectedForFolder.size} selected apps to a folder."); OutlinedTextField(value = folderName, onValueChange = { folderName = it.take(24) }, singleLine = true, label = { Text("Folder name") }) } },
            confirmButton = { TextButton(enabled = folderName.isNotBlank(), onClick = { onCreateHomeFolder(folderName, selectedForFolder.toList()); folderName = ""; selectedForFolder = emptySet(); showCreateFolderDialog = false }) { Text("Create") } },
            dismissButton = { TextButton(onClick = { showCreateFolderDialog = false }) { Text("Cancel") } },
        )
    }
    selectedFolderId?.let { folderId ->
        val folder = uiState.preferences.homeFolders.firstOrNull { it.id == folderId }
        if (folder == null) { selectedFolderId = null } else {
            AlertDialog(
                onDismissRequest = { selectedFolderId = null },
                title = { Text(folder.name) },
                text = { Column { folder.packageNames.forEach { packageName -> Text(uiState.allApps.firstOrNull { it.packageName == packageName }?.label ?: packageName, style = MaterialTheme.typography.bodyMedium) } } },
                confirmButton = { TextButton(onClick = { onDeleteHomeFolder(folder.id); selectedFolderId = null }) { Text("Delete folder") } },
                dismissButton = { TextButton(onClick = { selectedFolderId = null }) { Text("Close") } },
            )
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
