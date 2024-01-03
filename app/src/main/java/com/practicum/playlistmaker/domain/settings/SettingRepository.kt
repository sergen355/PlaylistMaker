package com.practicum.playlistmaker.domain.settings

interface SettingRepository {
    fun getStoredTheme(): Boolean
    fun setTheme(theme: Boolean)
}