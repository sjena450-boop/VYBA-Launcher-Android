package com.vyba.launcher.core.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import com.vyba.launcher.data.models.ThemeConfig
import com.vyba.launcher.data.models.ThemeMode

val LocalVybaThemeConfig = staticCompositionLocalOf { ThemeConfig() }

@Composable
fun VybaTheme(
    themeConfig: ThemeConfig,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val darkTheme = when (themeConfig.mode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val colorScheme = when {
        themeConfig.useDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> VybaColorTokens.darkSchemeFor(themeConfig.preset)
        else -> VybaColorTokens.lightSchemeFor(themeConfig.preset)
    }

    CompositionLocalProvider(LocalVybaThemeConfig provides themeConfig) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = VybaTypography,
            shapes = vybaShapesFor(themeConfig.cornerStyle),
            content = content,
        )
    }
}
