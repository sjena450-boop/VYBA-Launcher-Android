package com.vyba.launcher.data.models

import androidx.compose.ui.graphics.Color

enum class WallpaperPattern {
    RADIAL_GLOW,
    DIAGONAL_WAVE,
    AURORA_MESH,
    MINIMAL_HORIZON,
}

data class WallpaperPreset(
    val id: String,
    val title: String,
    val subtitle: String,
    val colors: List<Color>,
    val pattern: WallpaperPattern,
)

object VybaWallpapers {
    val presets: List<WallpaperPreset> = listOf(
        WallpaperPreset(
            id = "obsidian_nebula",
            title = "Obsidian Nebula",
            subtitle = "Deep violet & cyan radial glow",
            colors = listOf(Color(0xFF090B12), Color(0xFF3B1D7A), Color(0xFF0E7490)),
            pattern = WallpaperPattern.RADIAL_GLOW,
        ),
        WallpaperPreset(
            id = "nordic_glacier",
            title = "Nordic Glacier",
            subtitle = "Cool slate & arctic blue horizon",
            colors = listOf(Color(0xFF0F172A), Color(0xFF1D4ED8), Color(0xFF38BDF8)),
            pattern = WallpaperPattern.MINIMAL_HORIZON,
        ),
        WallpaperPreset(
            id = "amber_solstice",
            title = "Amber Solstice",
            subtitle = "Warm espresso & terracotta dusk",
            colors = listOf(Color(0xFF160F0D), Color(0xFF9A3412), Color(0xFFF59E0B)),
            pattern = WallpaperPattern.DIAGONAL_WAVE,
        ),
        WallpaperPreset(
            id = "emerald_aurora",
            title = "Emerald Aurora",
            subtitle = "Carbon forest & mint shimmer",
            colors = listOf(Color(0xFF07130F), Color(0xFF047857), Color(0xFF34D399)),
            pattern = WallpaperPattern.AURORA_MESH,
        ),
        WallpaperPreset(
            id = "midnight_velvet",
            title = "Midnight Velvet",
            subtitle = "Pure OLED black & indigo bloom",
            colors = listOf(Color(0xFF050508), Color(0xFF1E1B4B), Color(0xFF6366F1)),
            pattern = WallpaperPattern.RADIAL_GLOW,
        ),
    )
}
