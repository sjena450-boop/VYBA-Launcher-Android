package com.vyba.launcher.features.themes

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.vyba.launcher.core.theme.VybaColorTokens
import com.vyba.launcher.core.theme.VybaSpacing
import com.vyba.launcher.core.theme.components.VybaCard
import com.vyba.launcher.core.theme.components.VybaSectionHeader
import com.vyba.launcher.data.models.CornerStyle
import com.vyba.launcher.data.models.IconShape
import com.vyba.launcher.data.models.LauncherPreferences
import com.vyba.launcher.data.models.ThemeMode
import com.vyba.launcher.data.models.VybaPreset
import com.vyba.launcher.ui.components.iconClipShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeStudioScreen(
    preferences: LauncherPreferences,
    onSelectPreset: (VybaPreset) -> Unit,
    onSelectMode: (ThemeMode) -> Unit,
    onToggleDynamicColor: (Boolean) -> Unit,
    onSelectCornerStyle: (CornerStyle) -> Unit,
    onSelectIconShape: (IconShape) -> Unit,
    onSelectGridColumns: (Int) -> Unit,
    onToggleLabels: (Boolean) -> Unit,
    onBack: () -> Unit,
) {
    val theme = preferences.themeConfig
    val previewIconShape = iconClipShape(theme.iconShape)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Theme Studio") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = VybaSpacing.md),
            verticalArrangement = Arrangement.spacedBy(VybaSpacing.md),
        ) {
            // Live Launcher Mini-Preview
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f),
                        shape = MaterialTheme.shapes.large,
                    ),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.background,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                                    MaterialTheme.colorScheme.background,
                                ),
                            ),
                        )
                        .padding(VybaSpacing.md),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(VybaSpacing.sm)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "12:30",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                            Text(
                                text = if (theme.useDynamicColor) "Material You" else theme.preset.displayName,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }

                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Search,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp),
                                )
                                Text(
                                    text = "Search apps...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                        ) {
                            listOf(
                                Icons.Rounded.CameraAlt to "Camera",
                                Icons.Rounded.MusicNote to "Music",
                                Icons.Rounded.Apps to "Drawer",
                            ).forEach { (icon, label) ->
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(previewIconShape)
                                            .background(MaterialTheme.colorScheme.primaryContainer),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.size(20.dp),
                                        )
                                    }
                                    if (preferences.showAppLabels) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Theme Presets
            VybaSectionHeader(
                title = "Color Preset",
                subtitle = "Curated VYBA palettes",
            )
            VybaPreset.entries.forEach { preset ->
                val selected = theme.preset == preset && !theme.useDynamicColor
                val swatches = VybaColorTokens.swatchPreviewFor(preset)
                VybaCard(
                    highlighted = selected,
                    modifier = Modifier.clickable { onSelectPreset(preset) },
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = preset.displayName,
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Text(
                                text = preset.tagline,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Spacer(modifier = Modifier.width(VybaSpacing.sm))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            swatches.forEach { color ->
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(color),
                                )
                            }
                            if (selected) {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    }
                }
            }

            // Appearance Mode
            VybaSectionHeader(title = "Appearance Mode")
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                ThemeMode.entries.forEachIndexed { index, mode ->
                    SegmentedButton(
                        selected = theme.mode == mode,
                        onClick = { onSelectMode(mode) },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = ThemeMode.entries.size,
                        ),
                    ) {
                        Text(mode.label)
                    }
                }
            }

            // Material You Dynamic Color
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                VybaCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Material You Dynamic Color", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Extract system palette from your active wallpaper",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Switch(
                            checked = theme.useDynamicColor,
                            onCheckedChange = onToggleDynamicColor,
                        )
                    }
                }
            }

            // Corner Style
            VybaSectionHeader(title = "Corner Radius")
            Row(horizontalArrangement = Arrangement.spacedBy(VybaSpacing.sm)) {
                CornerStyle.entries.forEach { style ->
                    FilterChip(
                        selected = theme.cornerStyle == style,
                        onClick = { onSelectCornerStyle(style) },
                        label = { Text(style.label) },
                    )
                }
            }

            // Icon Shape
            VybaSectionHeader(title = "App Icon Shape")
            Row(horizontalArrangement = Arrangement.spacedBy(VybaSpacing.sm)) {
                IconShape.entries.forEach { shape ->
                    FilterChip(
                        selected = theme.iconShape == shape,
                        onClick = { onSelectIconShape(shape) },
                        label = { Text(shape.label) },
                    )
                }
            }

            // Grid Density & Labels
            VybaSectionHeader(title = "Home & Drawer Layout")
            Row(horizontalArrangement = Arrangement.spacedBy(VybaSpacing.sm)) {
                listOf(4, 5).forEach { cols ->
                    FilterChip(
                        selected = preferences.gridColumns == cols,
                        onClick = { onSelectGridColumns(cols) },
                        label = { Text("$cols Columns") },
                    )
                }
            }

            VybaCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Show App Labels", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Display app names below home and drawer icons",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Switch(
                        checked = preferences.showAppLabels,
                        onCheckedChange = onToggleLabels,
                    )
                }
            }

            Spacer(modifier = Modifier.height(VybaSpacing.lg))
        }
    }
}
