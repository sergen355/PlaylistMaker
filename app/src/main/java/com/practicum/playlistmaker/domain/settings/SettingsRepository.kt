package com.practicum.playlistmaker.domain.settings

interface SettingsRepository {
    fun getStoredTheme(): Boolean
    fun setTheme(theme: Boolean)
}