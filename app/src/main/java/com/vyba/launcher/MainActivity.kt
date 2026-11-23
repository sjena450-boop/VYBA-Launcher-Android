package com.vyba.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.vyba.launcher.core.theme.VybaTheme
import com.vyba.launcher.data.models.LauncherPreferences
import com.vyba.launcher.ui.navigation.VybaNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val prefsRepository = (application as VybaApplication).preferencesRepository

        setContent {
            val preferences by prefsRepository.preferencesFlow.collectAsState(
                initial = LauncherPreferences(),
            )
            VybaTheme(themeConfig = preferences.themeConfig) {
                VybaNavGraph()
            }
        }
    }
}
