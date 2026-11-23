package com.vyba.launcher.core.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp
import com.vyba.launcher.data.models.CornerStyle

fun vybaShapesFor(cornerStyle: CornerStyle): Shapes {
    val base = cornerStyle.radiusDp
    return Shapes(
        extraSmall = RoundedCornerShape((base / 2).coerceAtLeast(4).dp),
        small = RoundedCornerShape((base * 0.75f).toInt().dp),
        medium = RoundedCornerShape(base.dp),
        large = RoundedCornerShape((base + 6).dp),
        extraLarge = RoundedCornerShape((base + 12).dp),
    )
}
