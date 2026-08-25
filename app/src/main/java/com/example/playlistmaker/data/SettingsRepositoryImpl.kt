package com.example.playlistmaker.data

import android.content.SharedPreferences
import com.example.playlistmaker.domain.api.SettingsRepository

class SettingsRepositoryImpl(
    private val prefs: SharedPreferences
) : SettingsRepository {
    override fun isDarkThemeEnabled(): Boolean {
        return prefs.getBoolean(DARK_THEME_PREFERENCE, false)
    }

    override fun setDarkThemeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(DARK_THEME_PREFERENCE, enabled).apply()
    }
    companion object {
        private const val DARK_THEME_PREFERENCE = "dark_theme_preference"
    }
}