package com.vyba.launcher.data.models

enum class VybaPreset(
    val id: String,
    val displayName: String,
    val tagline: String,
) {
    OBSIDIAN_PULSE("obsidian_pulse", "Obsidian Pulse", "Deep obsidian with electric violet & cyan"),
    NORDIC_FROST("nordic_frost", "Nordic Frost", "Crisp slate & glacier blue clarity"),
    AMBER_DUSK("amber_dusk", "Amber Dusk", "Warm espresso & terracotta gold"),
    EMERALD_MONO("emerald_mono", "Emerald Mono", "Forest carbon & mint contrast");

    companion object {
        fun fromId(id: String?): VybaPreset =
            entries.firstOrNull { it.id == id } ?: OBSIDIAN_PULSE
    }
}

enum class ThemeMode(val id: String, val label: String) {
    SYSTEM("system", "System"),
    LIGHT("light", "Light"),
    DARK("dark", "Dark");

    companion object {
        fun fromId(id: String?): ThemeMode =
            entries.firstOrNull { it.id == id } ?: DARK
    }
}

enum class CornerStyle(val id: String, val label: String, val radiusDp: Int) {
    SHARP("sharp", "Sharp", 8),
    BALANCED("balanced", "Balanced", 16),
    ROUNDED("rounded", "Rounded", 24);

    companion object {
        fun fromId(id: String?): CornerStyle =
            entries.firstOrNull { it.id == id } ?: BALANCED
    }
}

enum class IconShape(val id: String, val label: String) {
    CIRCLE("circle", "Circle"),
    SQUIRCLE("squircle", "Squircle"),
    ROUNDED_SQUARE("rounded_square", "Rounded");

    companion object {
        fun fromId(id: String?): IconShape =
            entries.firstOrNull { it.id == id } ?: SQUIRCLE
    }
}

data class ThemeConfig(
    val preset: VybaPreset = VybaPreset.OBSIDIAN_PULSE,
    val mode: ThemeMode = ThemeMode.DARK,
    val useDynamicColor: Boolean = false,
    val cornerStyle: CornerStyle = CornerStyle.BALANCED,
    val iconShape: IconShape = IconShape.SQUIRCLE,
)

data class HomeFolder(
    val id: String,
    val name: String,
    val packageNames: List<String>,
)

data class LauncherPreferences(
    val themeConfig: ThemeConfig = ThemeConfig(),
    val gridColumns: Int = 4,
    val showAppLabels: Boolean = true,
    val pinnedPackages: Set<String> = emptySet(),
    val pinnedOrder: List<String> = emptyList(),
    val homeFolders: List<HomeFolder> = emptyList(),
    val dockPackages: List<String> = emptyList(),
    val hiddenPackages: Set<String> = emptySet(),
)
