package com.practicum.playlistmaker.domain.settings

interface SettingsInteractor {
    fun getTheme(): Boolean
    fun setTheme(theme: Boolean)
}