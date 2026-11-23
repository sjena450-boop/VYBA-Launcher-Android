package com.vyba.launcher.features.wallpapers

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.vyba.launcher.core.theme.VybaSpacing
import com.vyba.launcher.core.theme.components.VybaCard
import com.vyba.launcher.core.theme.components.VybaLoadingState
import com.vyba.launcher.core.theme.components.VybaPrimaryButton
import com.vyba.launcher.core.theme.components.VybaSecondaryButton
import com.vyba.launcher.data.models.WallpaperPattern
import com.vyba.launcher.data.models.WallpaperPreset
import com.vyba.launcher.data.repositories.WallpaperTarget

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WallpapersScreen(
    uiState: WallpapersUiState,
    onSelectPreset: (WallpaperPreset) -> Unit,
    onApplyWallpaper: (WallpaperTarget) -> Unit,
    onClearMessage: () -> Unit,
    onBack: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackbarHostState.showSnackbar(it)
            onClearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("VYBA Wallpapers") },
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
                .padding(horizontal = VybaSpacing.md),
            verticalArrangement = Arrangement.spacedBy(VybaSpacing.md),
        ) {
            // Selected Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(MaterialTheme.shapes.large),
            ) {
                WallpaperCanvas(
                    preset = uiState.selectedPreset,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            Column {
                Text(
                    text = uiState.selectedPreset.title,
                    style = MaterialTheme.typography.headlineMedium,
                )
                Text(
                    text = uiState.selectedPreset.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            // Preset Strip
            LazyRow(horizontalArrangement = Arrangement.spacedBy(VybaSpacing.sm)) {
                items(uiState.presets, key = { it.id }) { preset ->
                    VybaCard(
                        highlighted = preset.id == uiState.selectedPreset.id,
                        modifier = Modifier
                            .width(96.dp)
                            .clickable { onSelectPreset(preset) },
                    ) {
                        WallpaperCanvas(
                            preset = preset,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(0.75f)
                                .clip(MaterialTheme.shapes.small),
                        )
                    }
                }
            }

            if (uiState.isApplying) {
                VybaLoadingState(message = "Applying wallpaper...")
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = VybaSpacing.md),
                    horizontalArrangement = Arrangement.spacedBy(VybaSpacing.sm),
                ) {
                    VybaSecondaryButton(
                        text = "Home",
                        onClick = { onApplyWallpaper(WallpaperTarget.HOME) },
                        modifier = Modifier.weight(1f),
                    )
                    VybaSecondaryButton(
                        text = "Lock",
                        onClick = { onApplyWallpaper(WallpaperTarget.LOCK) },
                        modifier = Modifier.weight(1f),
                    )
                    VybaPrimaryButton(
                        text = "Both",
                        onClick = { onApplyWallpaper(WallpaperTarget.BOTH) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun WallpaperCanvas(
    preset: WallpaperPreset,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val brush = when (preset.pattern) {
            WallpaperPattern.RADIAL_GLOW -> Brush.radialGradient(
                colors = preset.colors.reversed(),
                center = Offset(size.width * 0.5f, size.height * 0.35f),
                radius = size.maxDimension * 0.7f,
            )
            WallpaperPattern.DIAGONAL_WAVE -> Brush.linearGradient(
                colors = preset.colors,
                start = Offset.Zero,
                end = Offset(size.width, size.height),
            )
            WallpaperPattern.AURORA_MESH -> Brush.linearGradient(
                colors = preset.colors,
                start = Offset(0f, size.height),
                end = Offset(size.width, 0f),
            )
            WallpaperPattern.MINIMAL_HORIZON -> Brush.verticalGradient(colors = preset.colors)
        }
        drawRect(brush = brush)
    }
}
