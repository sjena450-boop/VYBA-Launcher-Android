package com.vyba.launcher

import com.vyba.launcher.data.models.AppInfo
import com.vyba.launcher.features.home.HomeViewModel
import org.junit.Assert.assertEquals
import org.junit.Test

class AppFilterTest {
    private val sampleApps = listOf(
        AppInfo("com.android.camera", "Camera", "Camera", isSystemApp = true),
        AppInfo("com.spotify.music", "Main", "Spotify", isSystemApp = false),
        AppInfo("com.whatsapp", "Main", "WhatsApp", isSystemApp = false),
    )

    @Test
    fun filterVisibleApps_excludesHiddenAndMatchesQuery() {
        val result = HomeViewModel.filterVisibleApps(
            apps = sampleApps,
            query = "spot",
            hiddenPackages = setOf("com.whatsapp"),
        )
        assertEquals(1, result.size)
        assertEquals("com.spotify.music", result.first().packageName)
    }

    @Test
    fun filterVisibleApps_matchesPackageNameCaseInsensitively() {
        val result = HomeViewModel.filterVisibleApps(
            apps = sampleApps,
            query = "SPOTIFY",
            hiddenPackages = emptySet(),
        )
        assertEquals(listOf("com.spotify.music"), result.map { it.packageName })
    }

    @Test
    fun filterVisibleApps_emptyQueryReturnsAllNonHidden() {
        val result = HomeViewModel.filterVisibleApps(
            apps = sampleApps,
            query = "  ",
            hiddenPackages = setOf("com.android.camera"),
        )
        assertEquals(listOf("com.spotify.music", "com.whatsapp"), result.map { it.packageName })
    }
}
