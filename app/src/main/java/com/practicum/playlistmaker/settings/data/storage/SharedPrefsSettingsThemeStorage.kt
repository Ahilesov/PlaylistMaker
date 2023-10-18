package com.practicum.playlistmaker.settings.data.storage

import android.content.SharedPreferences
import com.practicum.playlistmaker.settings.data.SettingsThemeStorage
import com.practicum.playlistmaker.settings.domain.models.ThemeSettings
import com.practicum.playlistmaker.util.Constants

class SharedPrefsSettingsThemeStorage(private val sharedPreferences: SharedPreferences) :
    SettingsThemeStorage {

    override fun saveThemeSettings(settings: ThemeSettings) {
        sharedPreferences.edit()
            .putBoolean(Constants.DARK_THEME, settings.darkTheme)
            .apply()
    }

    override fun getThemeSettings(): ThemeSettings {
        val darkTheme = sharedPreferences.getBoolean(Constants.DARK_THEME, false)
        return ThemeSettings(darkTheme)
    }
}