package com.practicum.playlistmaker.data.settings

interface SettingRepository {
    fun getStoredTheme(): Boolean
    fun setTheme(theme: Boolean)
}