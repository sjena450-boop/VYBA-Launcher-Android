package com.vyba.launcher.data.repositories

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import androidx.compose.ui.graphics.toArgb
import com.vyba.launcher.data.models.WallpaperPattern
import com.vyba.launcher.data.models.WallpaperPreset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

enum class WallpaperTarget(val label: String) {
    HOME("Home Screen"),
    LOCK("Lock Screen"),
    BOTH("Home & Lock"),
}

class WallpaperApplier(private val context: Context) {

    suspend fun applyWallpaper(
        preset: WallpaperPreset,
        target: WallpaperTarget,
    ): Result<Unit> = withContext(Dispatchers.Default) {
        runCatching {
            val bitmap = renderWallpaperBitmap(preset, width = 1080, height = 2400)
            try {
                val manager = WallpaperManager.getInstance(context)
                val flag = when (target) {
                    WallpaperTarget.HOME -> WallpaperManager.FLAG_SYSTEM
                    WallpaperTarget.LOCK -> WallpaperManager.FLAG_LOCK
                    WallpaperTarget.BOTH -> WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
                }
                manager.setBitmap(bitmap, null, true, flag)
                Unit
            } finally {
                bitmap.recycle()
            }
        }
    }

    private fun renderWallpaperBitmap(
        preset: WallpaperPreset,
        width: Int,
        height: Int,
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val argbColors = preset.colors.map { it.toArgb() }.toIntArray()
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        when (preset.pattern) {
            WallpaperPattern.RADIAL_GLOW -> {
                canvas.drawColor(argbColors.first())
                paint.shader = RadialGradient(
                    width * 0.5f,
                    height * 0.35f,
                    width * 0.95f,
                    intArrayOf(argbColors.last(), argbColors[1], argbColors.first()),
                    floatArrayOf(0f, 0.55f, 1f),
                    Shader.TileMode.CLAMP,
                )
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            }

            WallpaperPattern.DIAGONAL_WAVE -> {
                paint.shader = LinearGradient(
                    0f,
                    0f,
                    width.toFloat(),
                    height.toFloat(),
                    argbColors,
                    null,
                    Shader.TileMode.CLAMP,
                )
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            }

            WallpaperPattern.AURORA_MESH -> {
                canvas.drawColor(argbColors.first())
                paint.shader = LinearGradient(
                    0f,
                    height.toFloat(),
                    width.toFloat(),
                    0f,
                    argbColors,
                    null,
                    Shader.TileMode.CLAMP,
                )
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            }

            WallpaperPattern.MINIMAL_HORIZON -> {
                paint.shader = LinearGradient(
                    0f,
                    0f,
                    0f,
                    height.toFloat(),
                    argbColors,
                    floatArrayOf(0f, 0.55f, 1f),
                    Shader.TileMode.CLAMP,
                )
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            }
        }
        return bitmap
    }
}
