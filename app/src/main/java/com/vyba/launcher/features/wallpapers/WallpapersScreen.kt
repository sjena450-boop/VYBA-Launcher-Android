package com.vyba.launcher.features.wallpapers

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            // Phone Mockup Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .aspectRatio(0.52f)
                        .clip(RoundedCornerShape(28.dp))
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(28.dp),
                        ),
                ) {
                    WallpaperCanvas(
                        preset = uiState.selectedPreset,
                        modifier = Modifier.fillMaxSize(),
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(horizontalAlignment = Alignment.Start) {
                            Text(
                                text = "12:30",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Light,
                                color = Color.White,
                            )
                            Text(
                                text = "Monday, Nov 23",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White.copy(alpha = 0.8f),
                            )
                        }
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(34.dp),
                            shape = RoundedCornerShape(18.dp),
                            color = Color.Black.copy(alpha = 0.35f),
                        ) {}
                    }
                }
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
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(preset.colors.first(), preset.colors[1]),
            ),
        )
        when (preset.pattern) {
            WallpaperPattern.RADIAL_GLOW -> {
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(preset.colors.last(), Color.Transparent),
                        center = Offset(size.width * 0.5f, size.height * 0.34f),
                        radius = size.maxDimension * 0.65f,
                    ),
                )
            }
            WallpaperPattern.DIAGONAL_WAVE -> {
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(preset.colors.last(), Color.Transparent),
                        center = Offset(size.width * 0.85f, size.height * 0.25f),
                        radius = size.maxDimension * 0.7f,
                    ),
                )
            }
            WallpaperPattern.AURORA_MESH -> {
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(preset.colors.last(), Color.Transparent),
                        center = Offset(size.width * 0.2f, size.height * 0.28f),
                        radius = size.maxDimension * 0.7f,
                    ),
                )
            }
            WallpaperPattern.MINIMAL_HORIZON -> {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, preset.colors.last(), preset.colors.first()),
                    ),
                )
            }
        }
    }
}
