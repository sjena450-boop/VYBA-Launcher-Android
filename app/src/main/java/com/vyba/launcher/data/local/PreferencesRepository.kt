package com.vyba.launcher.data.local

import android.content.Context
import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.vyba.launcher.data.models.CornerStyle
import com.vyba.launcher.data.models.HomeFolder
import com.vyba.launcher.data.models.IconShape
import com.vyba.launcher.data.models.LauncherPreferences
import com.vyba.launcher.data.models.ThemeConfig
import com.vyba.launcher.data.models.ThemeMode
import com.vyba.launcher.data.models.VybaPreset
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.vybaDataStore: DataStore<Preferences> by preferencesDataStore(name = "vyba_preferences")

class PreferencesRepository(private val context: Context) {

    private object Keys {
        val THEME_PRESET = stringPreferencesKey("theme_preset")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val CORNER_STYLE = stringPreferencesKey("corner_style")
        val ICON_SHAPE = stringPreferencesKey("icon_shape")
        val GRID_COLUMNS = intPreferencesKey("grid_columns")
        val SHOW_APP_LABELS = booleanPreferencesKey("show_app_labels")
        val PINNED_PACKAGES = stringSetPreferencesKey("pinned_packages")
        val DOCK_PACKAGES = stringPreferencesKey("dock_packages")
        val HIDDEN_PACKAGES = stringSetPreferencesKey("hidden_packages")
        val PINNED_ORDER = stringPreferencesKey("pinned_order")
        val HOME_FOLDERS = stringPreferencesKey("home_folders")
    }

    val preferencesFlow: Flow<LauncherPreferences> = context.vybaDataStore.data.map { prefs ->
        val themeConfig = ThemeConfig(
            preset = VybaPreset.fromId(prefs[Keys.THEME_PRESET]),
            mode = ThemeMode.fromId(prefs[Keys.THEME_MODE]),
            useDynamicColor = prefs[Keys.DYNAMIC_COLOR] ?: false,
            cornerStyle = CornerStyle.fromId(prefs[Keys.CORNER_STYLE]),
            iconShape = IconShape.fromId(prefs[Keys.ICON_SHAPE]),
        )
        val dockRaw = prefs[Keys.DOCK_PACKAGES].orEmpty()
        val dockList = if (dockRaw.isBlank()) emptyList() else dockRaw.split("|").filter { it.isNotBlank() }

        LauncherPreferences(
            themeConfig = themeConfig,
            gridColumns = (prefs[Keys.GRID_COLUMNS] ?: 4).coerceIn(4, 5),
            showAppLabels = prefs[Keys.SHOW_APP_LABELS] ?: true,
            pinnedPackages = prefs[Keys.PINNED_PACKAGES] ?: emptySet(),
            pinnedOrder = prefs[Keys.PINNED_ORDER].orEmpty().split("|").filter { it.isNotBlank() },
            homeFolders = decodeFolders(prefs[Keys.HOME_FOLDERS].orEmpty()),
            dockPackages = dockList,
            hiddenPackages = prefs[Keys.HIDDEN_PACKAGES] ?: emptySet(),
        )
    }

    suspend fun setPreset(preset: VybaPreset) {
        context.vybaDataStore.edit { it[Keys.THEME_PRESET] = preset.id }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.vybaDataStore.edit { it[Keys.THEME_MODE] = mode.id }
    }

    suspend fun setDynamicColor(enabled: Boolean) {
        context.vybaDataStore.edit { it[Keys.DYNAMIC_COLOR] = enabled }
    }

    suspend fun setCornerStyle(style: CornerStyle) {
        context.vybaDataStore.edit { it[Keys.CORNER_STYLE] = style.id }
    }

    suspend fun setIconShape(shape: IconShape) {
        context.vybaDataStore.edit { it[Keys.ICON_SHAPE] = shape.id }
    }

    suspend fun setGridColumns(columns: Int) {
        context.vybaDataStore.edit { it[Keys.GRID_COLUMNS] = columns.coerceIn(4, 5) }
    }

    suspend fun setShowAppLabels(show: Boolean) {
        context.vybaDataStore.edit { it[Keys.SHOW_APP_LABELS] = show }
    }

    suspend fun togglePinnedPackage(packageName: String) {
        context.vybaDataStore.edit { prefs ->
            val current = prefs[Keys.PINNED_PACKAGES] ?: emptySet()
            val removing = packageName in current
            prefs[Keys.PINNED_PACKAGES] = if (removing) current - packageName else current + packageName
            if (removing) {
                val folders = decodeFolders(prefs[Keys.HOME_FOLDERS].orEmpty()).mapNotNull { folder ->
                    val remaining = folder.packageNames - packageName
                    if (remaining.size < 2) null else folder.copy(packageNames = remaining)
                }
                prefs[Keys.HOME_FOLDERS] = encodeFolders(folders)
                val order = prefs[Keys.PINNED_ORDER].orEmpty().split("|").filter { it.isNotBlank() && it != packageName }
                prefs[Keys.PINNED_ORDER] = order.joinToString("|")
            }
        }
    }

    suspend fun movePinnedPackage(packageName: String, targetIndex: Int) {
        context.vybaDataStore.edit { prefs ->
            val pinned = prefs[Keys.PINNED_PACKAGES] ?: emptySet()
            if (packageName !in pinned) return@edit
            val stored = prefs[Keys.PINNED_ORDER].orEmpty()
                .split("|").filter { it.isNotBlank() && it in pinned }.toMutableList()
            (pinned - stored.toSet()).sorted().forEach(stored::add)
            val oldIndex = stored.indexOf(packageName)
            if (oldIndex < 0) return@edit
            val moved = stored.removeAt(oldIndex)
            stored.add(targetIndex.coerceIn(0, stored.size), moved)
            prefs[Keys.PINNED_ORDER] = stored.joinToString("|")
        }
    }

    suspend fun toggleDockPackage(packageName: String) {
        context.vybaDataStore.edit { prefs ->
            val raw = prefs[Keys.DOCK_PACKAGES].orEmpty()
            val current = if (raw.isBlank()) mutableListOf() else raw.split("|").filter { it.isNotBlank() }.toMutableList()
            if (packageName in current) {
                current.remove(packageName)
            } else if (current.size < 4) {
                current.add(packageName)
            }
            prefs[Keys.DOCK_PACKAGES] = current.joinToString("|")
        }
    }

    suspend fun toggleHiddenPackage(packageName: String) {
        context.vybaDataStore.edit { prefs ->
            val current = prefs[Keys.HIDDEN_PACKAGES] ?: emptySet()
            prefs[Keys.HIDDEN_PACKAGES] = if (packageName in current) {
                current - packageName
            } else {
                current + packageName
            }
        }
    }


    suspend fun createHomeFolder(name: String, packageNames: List<String>) {
        val cleanName = name.trim().take(24)
        val members = packageNames.distinct()
        if (cleanName.isBlank() || members.size < 2) return
        context.vybaDataStore.edit { prefs ->
            val pinned = prefs[Keys.PINNED_PACKAGES] ?: emptySet()
            if (members.any { it !in pinned }) return@edit
            val folders = decodeFolders(prefs[Keys.HOME_FOLDERS].orEmpty()).toMutableList()
            val alreadyGrouped = folders.flatMap { it.packageNames }.toSet()
            if (members.any { it in alreadyGrouped }) return@edit
            val id = java.util.UUID.randomUUID().toString()
            folders.add(HomeFolder(id, cleanName, members))
            prefs[Keys.HOME_FOLDERS] = encodeFolders(folders)
        }
    }

    suspend fun renameHomeFolder(folderId: String, name: String) {
        val cleanName = name.trim().take(24)
        if (cleanName.isBlank()) return
        context.vybaDataStore.edit { prefs ->
            val folders = decodeFolders(prefs[Keys.HOME_FOLDERS].orEmpty()).map { folder ->
                if (folder.id == folderId) folder.copy(name = cleanName) else folder
            }
            prefs[Keys.HOME_FOLDERS] = encodeFolders(folders)
        }
    }

    suspend fun deleteHomeFolder(folderId: String) {
        context.vybaDataStore.edit { prefs ->
            val folders = decodeFolders(prefs[Keys.HOME_FOLDERS].orEmpty())
                .filterNot { it.id == folderId }
            prefs[Keys.HOME_FOLDERS] = encodeFolders(folders)
        }
    }

    private fun encodePart(value: String): String =
        Base64.encodeToString(value.toByteArray(Charsets.UTF_8), Base64.NO_WRAP or Base64.URL_SAFE)

    private fun decodePart(value: String): String =
        String(Base64.decode(value, Base64.NO_WRAP or Base64.URL_SAFE), Charsets.UTF_8)

    private fun encodeFolders(folders: List<HomeFolder>): String =
        folders.joinToString("|") { folder ->
            listOf(folder.id, folder.name, folder.packageNames.joinToString(","))
                .joinToString(":") { encodePart(it) }
        }

    private fun decodeFolders(raw: String): List<HomeFolder> =
        raw.split("|").mapNotNull { record ->
            runCatching {
                val parts = record.split(":")
                if (parts.size != 3) return@runCatching null
                val members = decodePart(parts[2]).split(",").filter { it.isNotBlank() }
                HomeFolder(decodePart(parts[0]), decodePart(parts[1]), members)
                    .takeIf { it.name.isNotBlank() && it.packageNames.size >= 2 }
            }.getOrNull()
        }

    suspend fun resetAll() {
        context.vybaDataStore.edit { it.clear() }
    }
}
