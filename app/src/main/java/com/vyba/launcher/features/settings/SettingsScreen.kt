package com.vyba.launcher.features.settings

import android.app.role.RoleManager
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.vyba.launcher.BuildConfig
import com.vyba.launcher.core.theme.VybaSpacing
import com.vyba.launcher.core.theme.components.VybaCard
import com.vyba.launcher.core.theme.components.VybaPrimaryButton
import com.vyba.launcher.core.theme.components.VybaSecondaryButton
import com.vyba.launcher.core.theme.components.VybaSectionHeader
import com.vyba.launcher.data.models.AppInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    hiddenApps: List<AppInfo>,
    onUnhideApp: (String) -> Unit,
    onResetPreferences: () -> Unit,
    onOpenThemeStudio: () -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    var showHiddenDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    val roleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("VYBA Settings") },
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
            VybaSectionHeader(
                title = "Default Home Launcher",
                subtitle = "Use VYBA as your primary Android home screen",
            )
            VybaCard {
                Text(
                    text = "Set VYBA as Default Launcher",
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(VybaSpacing.xs))
                Text(
                    text = "Pressing your device Home button will open VYBA.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(VybaSpacing.md))
                VybaPrimaryButton(
                    text = "Choose Default Launcher",
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            val roleManager = context.getSystemService(RoleManager::class.java)
                            if (roleManager != null && roleManager.isRoleAvailable(RoleManager.ROLE_HOME) &&
                                !roleManager.isRoleHeld(RoleManager.ROLE_HOME)
                            ) {
                                roleLauncher.launch(roleManager.createRequestRoleIntent(RoleManager.ROLE_HOME))
                                return@VybaPrimaryButton
                            }
                        }
                        context.startActivity(Intent(Settings.ACTION_HOME_SETTINGS))
                    },
                )
            }

            VybaSectionHeader(title = "Customization & Apps")
            VybaCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Theme & Icon Studio", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Customize colors, radius, icon shape, and grid",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    VybaSecondaryButton(text = "Open", onClick = onOpenThemeStudio)
                }
            }

            VybaCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Hidden Apps", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "${hiddenApps.size} app(s) hidden from drawer",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    VybaSecondaryButton(
                        text = "Manage",
                        onClick = { showHiddenDialog = true },
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
                        Text("Reset VYBA Preferences", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Restore default theme, pins, dock, and hidden apps",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    VybaSecondaryButton(
                        text = "Reset",
                        onClick = { showResetDialog = true },
                    )
                }
            }

            VybaCard {
                Text("About VYBA", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(VybaSpacing.xs))
                Text(
                    text = "Version ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE}) • ${BuildConfig.APPLICATION_ID}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(VybaSpacing.lg))
        }
    }

    if (showHiddenDialog) {
        AlertDialog(
            onDismissRequest = { showHiddenDialog = false },
            title = { Text("Hidden Apps") },
            text = {
                if (hiddenApps.isEmpty()) {
                    Text("No apps are currently hidden.")
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(VybaSpacing.xs)) {
                        hiddenApps.forEach { app ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(app.label, style = MaterialTheme.typography.bodyLarge)
                                TextButton(onClick = { onUnhideApp(app.packageName) }) {
                                    Text("Unhide")
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showHiddenDialog = false }) {
                    Text("Done")
                }
            },
        )
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset all preferences?") },
            text = { Text("This will restore Obsidian Pulse defaults and clear pinned, docked, and hidden app lists.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResetDialog = false
                        onResetPreferences()
                    },
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            },
        )
    }
}
