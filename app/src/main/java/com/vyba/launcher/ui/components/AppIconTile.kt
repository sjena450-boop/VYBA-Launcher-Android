package com.vyba.launcher.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.vyba.launcher.core.theme.LocalVybaThemeConfig
import com.vyba.launcher.data.models.AppInfo
import com.vyba.launcher.data.models.IconShape

fun iconClipShape(shape: IconShape): Shape = when (shape) {
    IconShape.CIRCLE -> CircleShape
    IconShape.SQUIRCLE -> RoundedCornerShape(28)
    IconShape.ROUNDED_SQUARE -> RoundedCornerShape(14.dp)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppIconTile(
    app: AppInfo,
    showLabel: Boolean,
    isPinned: Boolean,
    isDocked: Boolean,
    onLaunch: () -> Unit,
    onTogglePin: () -> Unit,
    onToggleDock: () -> Unit,
    onHideApp: () -> Unit,
    onAppInfo: () -> Unit,
    onUninstall: () -> Unit,
    modifier: Modifier = Modifier,
    iconSize: Dp = 56.dp,
) {
    val themeConfig = LocalVybaThemeConfig.current
    val clipShape = iconClipShape(themeConfig.iconShape)
    var menuExpanded by remember { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "iconPressScale",
    )

    val bitmap = remember(app.packageName, app.icon) {
        app.icon?.let { runCatching { it.toBitmap().asImageBitmap() }.getOrNull() }
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(MaterialTheme.shapes.small)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onLaunch,
                onLongClick = { menuExpanded = true },
            )
            .padding(vertical = 6.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(iconSize)
                    .shadow(elevation = 6.dp, shape = clipShape, clip = false)
                    .clip(clipShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f))
                    .border(
                        width = 0.75.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.14f),
                        shape = clipShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = app.label,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Text(
                        text = app.label.take(1).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            if (showLabel) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = app.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                )
            }
        }

        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
        ) {
            DropdownMenuItem(
                text = { Text(if (isPinned) "Unpin from Home" else "Pin to Home") },
                onClick = {
                    menuExpanded = false
                    onTogglePin()
                },
            )
            DropdownMenuItem(
                text = { Text(if (isDocked) "Remove from Dock" else "Add to Dock") },
                onClick = {
                    menuExpanded = false
                    onToggleDock()
                },
            )
            DropdownMenuItem(
                text = { Text("Hide from Drawer") },
                onClick = {
                    menuExpanded = false
                    onHideApp()
                },
            )
            DropdownMenuItem(
                text = { Text("App Info") },
                onClick = {
                    menuExpanded = false
                    onAppInfo()
                },
            )
            if (!app.isSystemApp) {
                DropdownMenuItem(
                    text = { Text("Uninstall") },
                    onClick = {
                        menuExpanded = false
                        onUninstall()
                    },
                )
            }
        }
    }
}
