package com.practicum.playlistmaker.domain.settings

interface SettingInteractor {
    fun getTheme(): Boolean
    fun setTheme(theme: Boolean)
}