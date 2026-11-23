package com.vyba.launcher.core.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.vyba.launcher.data.models.VybaPreset

object VybaColorTokens {
    fun darkSchemeFor(preset: VybaPreset): ColorScheme = when (preset) {
        VybaPreset.OBSIDIAN_PULSE -> darkColorScheme(
            primary = Color(0xFF9D7BFF),
            onPrimary = Color(0xFF1A0847),
            primaryContainer = Color(0xFF321B6E),
            onPrimaryContainer = Color(0xFFE7DEFF),
            secondary = Color(0xFF22D3EE),
            onSecondary = Color(0xFF00363F),
            secondaryContainer = Color(0xFF0A4B57),
            onSecondaryContainer = Color(0xFFA5F3FC),
            tertiary = Color(0xFFF472B6),
            background = Color(0xFF0B0D14),
            onBackground = Color(0xFFF1F3F9),
            surface = Color(0xFF121521),
            onSurface = Color(0xFFF1F3F9),
            surfaceVariant = Color(0xFF1C2031),
            onSurfaceVariant = Color(0xFFB4BBD0),
            outline = Color(0xFF353C54),
        )

        VybaPreset.NORDIC_FROST -> darkColorScheme(
            primary = Color(0xFF60A5FA),
            onPrimary = Color(0xFF0A244D),
            primaryContainer = Color(0xFF1E3A6E),
            onPrimaryContainer = Color(0xFFDBEAFE),
            secondary = Color(0xFF38BDF8),
            onSecondary = Color(0xFF052E42),
            secondaryContainer = Color(0xFF0F445E),
            onSecondaryContainer = Color(0xFFE0F2FE),
            background = Color(0xFF0C111C),
            onBackground = Color(0xFFF1F5F9),
            surface = Color(0xFF131A29),
            onSurface = Color(0xFFF1F5F9),
            surfaceVariant = Color(0xFF1D263B),
            onSurfaceVariant = Color(0xFF94A3B8),
            outline = Color(0xFF334155),
        )

        VybaPreset.AMBER_DUSK -> darkColorScheme(
            primary = Color(0xFFFB923C),
            onPrimary = Color(0xFF451900),
            primaryContainer = Color(0xFF6B2A09),
            onPrimaryContainer = Color(0xFFFFEDD5),
            secondary = Color(0xFFFBBF24),
            onSecondary = Color(0xFF3F2A00),
            secondaryContainer = Color(0xFF5C4008),
            onSecondaryContainer = Color(0xFFFEF3C7),
            background = Color(0xFF14100E),
            onBackground = Color(0xFFFAF5F0),
            surface = Color(0xFF1D1714),
            onSurface = Color(0xFFFAF5F0),
            surfaceVariant = Color(0xFF2B221E),
            onSurfaceVariant = Color(0xFFCBB8AD),
            outline = Color(0xFF4A3B34),
        )

        VybaPreset.EMERALD_MONO -> darkColorScheme(
            primary = Color(0xFF34D399),
            onPrimary = Color(0xFF003824),
            primaryContainer = Color(0xFF065F46),
            onPrimaryContainer = Color(0xFFD1FAE5),
            secondary = Color(0xFFA7F3D0),
            onSecondary = Color(0xFF053323),
            secondaryContainer = Color(0xFF114B36),
            onSecondaryContainer = Color(0xFFECFDF5),
            background = Color(0xFF08120E),
            onBackground = Color(0xFFECFDF5),
            surface = Color(0xFF0E1B16),
            onSurface = Color(0xFFECFDF5),
            surfaceVariant = Color(0xFF172922),
            onSurfaceVariant = Color(0xFF9BC2B2),
            outline = Color(0xFF2B473C),
        )
    }

    fun lightSchemeFor(preset: VybaPreset): ColorScheme = when (preset) {
        VybaPreset.OBSIDIAN_PULSE -> lightColorScheme(
            primary = Color(0xFF6D28D9),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFEDE9FE),
            onPrimaryContainer = Color(0xFF2E1065),
            secondary = Color(0xFF0891B2),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFCFFAFE),
            onSecondaryContainer = Color(0xFF083344),
            background = Color(0xFFF7F7FB),
            onBackground = Color(0xFF11131D),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF11131D),
            surfaceVariant = Color(0xFFECEEF6),
            onSurfaceVariant = Color(0xFF4B5268),
            outline = Color(0xFFCBD1E1),
        )

        VybaPreset.NORDIC_FROST -> lightColorScheme(
            primary = Color(0xFF2563EB),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFDBEAFE),
            onPrimaryContainer = Color(0xFF1E3A8A),
            secondary = Color(0xFF0284C7),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFE0F2FE),
            onSecondaryContainer = Color(0xFF0C4A6E),
            background = Color(0xFFF5F8FC),
            onBackground = Color(0xFF0F172A),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF0F172A),
            surfaceVariant = Color(0xFFE2E8F0),
            onSurfaceVariant = Color(0xFF475569),
            outline = Color(0xFFCBD5E1),
        )

        VybaPreset.AMBER_DUSK -> lightColorScheme(
            primary = Color(0xFFEA580C),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFFFEDD5),
            onPrimaryContainer = Color(0xFF7C2D12),
            secondary = Color(0xFFD97706),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFFEF3C7),
            onSecondaryContainer = Color(0xFF78350F),
            background = Color(0xFFFAF6F0),
            onBackground = Color(0xFF1C1410),
            surface = Color(0xFFFFFCF8),
            onSurface = Color(0xFF1C1410),
            surfaceVariant = Color(0xFFF1E6DA),
            onSurfaceVariant = Color(0xFF5E4B3E),
            outline = Color(0xFFD9C7B8),
        )

        VybaPreset.EMERALD_MONO -> lightColorScheme(
            primary = Color(0xFF059669),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFD1FAE5),
            onPrimaryContainer = Color(0xFF064E3B),
            secondary = Color(0xFF0D9488),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFCCFBF1),
            onSecondaryContainer = Color(0xFF134E4A),
            background = Color(0xFFF3FAF7),
            onBackground = Color(0xFF0A1914),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF0A1914),
            surfaceVariant = Color(0xFFE0EFE8),
            onSurfaceVariant = Color(0xFF3E5C50),
            outline = Color(0xFFBBD6CA),
        )
    }

    fun swatchPreviewFor(preset: VybaPreset): List<Color> = when (preset) {
        VybaPreset.OBSIDIAN_PULSE -> listOf(Color(0xFF0B0D14), Color(0xFF8B5CF6), Color(0xFF22D3EE))
        VybaPreset.NORDIC_FROST -> listOf(Color(0xFF0C111C), Color(0xFF3B82F6), Color(0xFF38BDF8))
        VybaPreset.AMBER_DUSK -> listOf(Color(0xFF14100E), Color(0xFFF97316), Color(0xFFFBBF24))
        VybaPreset.EMERALD_MONO -> listOf(Color(0xFF08120E), Color(0xFF10B981), Color(0xFFA7F3D0))
    }
}
