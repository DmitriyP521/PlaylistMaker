package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

const val APPLICATION_PREFERENCES = "application_preferences"

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        applyStoredTheme()
    }

    private fun applyStoredTheme() {
        val settingsInteractor = Creator.provideSettingsInteractor(this)
        val isDark = settingsInteractor.isDarkThemeEnabled()

        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}