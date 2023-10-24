package com.practicum.playlistmaker.settings.data

import com.practicum.playlistmaker.settings.domain.models.ThemeSettings

interface SettingsThemeStorage {
    fun saveThemeSettings(settings: ThemeSettings)
    fun getThemeSettings(): ThemeSettings
}