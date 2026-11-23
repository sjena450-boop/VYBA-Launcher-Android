package com.vyba.launcher.features.themes

import android.os.Build
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.vyba.launcher.core.theme.VybaColorTokens
import com.vyba.launcher.core.theme.VybaSpacing
import com.vyba.launcher.core.theme.components.VybaCard
import com.vyba.launcher.core.theme.components.VybaPrimaryButton
import com.vyba.launcher.core.theme.components.VybaSecondaryButton
import com.vyba.launcher.core.theme.components.VybaSectionHeader
import com.vyba.launcher.data.models.CornerStyle
import com.vyba.launcher.data.models.IconShape
import com.vyba.launcher.data.models.LauncherPreferences
import com.vyba.launcher.data.models.ThemeMode
import com.vyba.launcher.data.models.VybaPreset

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
            // Live Preview Card
            VybaCard(highlighted = true) {
                Text(
                    text = "Live Preview — ${theme.preset.displayName}",
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(VybaSpacing.xs))
                Text(
                    text = "Changes apply immediately across your launcher, drawer, and cards.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(VybaSpacing.md))
                Row(horizontalArrangement = Arrangement.spacedBy(VybaSpacing.sm)) {
                    VybaPrimaryButton(text = "Primary", onClick = {})
                    VybaSecondaryButton(text = "Secondary", onClick = {})
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
