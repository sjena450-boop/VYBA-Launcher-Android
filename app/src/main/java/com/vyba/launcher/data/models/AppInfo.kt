package com.vyba.launcher.data.models

import android.graphics.drawable.Drawable

data class AppInfo(
    val packageName: String,
    val activityName: String,
    val label: String,
    val isSystemApp: Boolean,
    val icon: Drawable? = null,
)
