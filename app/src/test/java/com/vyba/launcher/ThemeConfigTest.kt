package com.vyba.launcher

import com.vyba.launcher.data.models.CornerStyle
import com.vyba.launcher.data.models.IconShape
import com.vyba.launcher.data.models.ThemeMode
import com.vyba.launcher.data.models.VybaPreset
import org.junit.Assert.assertEquals
import org.junit.Test

class ThemeConfigTest {
    @Test
    fun presetFromId_returnsMatchingOrDefault() {
        assertEquals(VybaPreset.NORDIC_FROST, VybaPreset.fromId("nordic_frost"))
        assertEquals(VybaPreset.OBSIDIAN_PULSE, VybaPreset.fromId("unknown_id"))
        assertEquals(VybaPreset.OBSIDIAN_PULSE, VybaPreset.fromId(null))
    }

    @Test
    fun themeModeAndShapes_fallbackSafely() {
        assertEquals(ThemeMode.LIGHT, ThemeMode.fromId("light"))
        assertEquals(ThemeMode.DARK, ThemeMode.fromId("invalid"))
        assertEquals(CornerStyle.ROUNDED, CornerStyle.fromId("rounded"))
        assertEquals(CornerStyle.BALANCED, CornerStyle.fromId(null))
        assertEquals(IconShape.CIRCLE, IconShape.fromId("circle"))
        assertEquals(IconShape.SQUIRCLE, IconShape.fromId("bad"))
    }
}
