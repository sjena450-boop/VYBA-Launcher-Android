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
        val w = width.toFloat()
        val h = height.toFloat()

        // Base backdrop
        paint.shader = LinearGradient(
            0f, 0f, 0f, h,
            argbColors.first(),
            argbColors[1],
            Shader.TileMode.CLAMP,
        )
        canvas.drawRect(0f, 0f, w, h, paint)

        // Layered atmospheric glow
        val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        when (preset.pattern) {
            WallpaperPattern.RADIAL_GLOW -> {
                glowPaint.shader = RadialGradient(
                    w * 0.5f, h * 0.34f, w * 0.85f,
                    intArrayOf(argbColors.last(), argbColors[1], 0x00000000),
                    floatArrayOf(0f, 0.45f, 1f),
                    Shader.TileMode.CLAMP,
                )
                canvas.drawRect(0f, 0f, w, h, glowPaint)
            }

            WallpaperPattern.DIAGONAL_WAVE -> {
                glowPaint.shader = RadialGradient(
                    w * 0.85f, h * 0.25f, w * 0.9f,
                    intArrayOf(argbColors.last(), 0x00000000),
                    floatArrayOf(0f, 1f),
                    Shader.TileMode.CLAMP,
                )
                canvas.drawRect(0f, 0f, w, h, glowPaint)
                glowPaint.shader = RadialGradient(
                    w * 0.15f, h * 0.78f, w * 0.8f,
                    intArrayOf(argbColors[1], 0x00000000),
                    floatArrayOf(0f, 1f),
                    Shader.TileMode.CLAMP,
                )
                canvas.drawRect(0f, 0f, w, h, glowPaint)
            }

            WallpaperPattern.AURORA_MESH -> {
                glowPaint.shader = RadialGradient(
                    w * 0.2f, h * 0.28f, w * 0.85f,
                    intArrayOf(argbColors.last(), 0x00000000),
                    floatArrayOf(0f, 1f),
                    Shader.TileMode.CLAMP,
                )
                canvas.drawRect(0f, 0f, w, h, glowPaint)
                glowPaint.shader = RadialGradient(
                    w * 0.82f, h * 0.65f, w * 0.85f,
                    intArrayOf(argbColors[1], 0x00000000),
                    floatArrayOf(0f, 1f),
                    Shader.TileMode.CLAMP,
                )
                canvas.drawRect(0f, 0f, w, h, glowPaint)
            }

            WallpaperPattern.MINIMAL_HORIZON -> {
                glowPaint.shader = LinearGradient(
                    0f, h * 0.45f, 0f, h,
                    intArrayOf(0x00000000, argbColors.last(), argbColors.first()),
                    floatArrayOf(0f, 0.45f, 1f),
                    Shader.TileMode.CLAMP,
                )
                canvas.drawRect(0f, 0f, w, h, glowPaint)
            }
        }
        return bitmap
    }
}
