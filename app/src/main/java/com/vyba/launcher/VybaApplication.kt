package com.vyba.launcher

import android.app.Application
import com.vyba.launcher.data.local.PreferencesRepository
import com.vyba.launcher.data.repositories.AppRepository
import com.vyba.launcher.data.repositories.WallpaperApplier

class VybaApplication : Application() {
    lateinit var preferencesRepository: PreferencesRepository
        private set

    lateinit var appRepository: AppRepository
        private set

    lateinit var wallpaperApplier: WallpaperApplier
        private set

    override fun onCreate() {
        super.onCreate()
        preferencesRepository = PreferencesRepository(this)
        appRepository = AppRepository(this)
        wallpaperApplier = WallpaperApplier(this)
    }
}
