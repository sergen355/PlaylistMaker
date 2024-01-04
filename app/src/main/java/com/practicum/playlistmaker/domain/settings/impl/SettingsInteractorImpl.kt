package com.practicum.playlistmaker.domain.settings.impl

import com.practicum.playlistmaker.domain.settings.SettingsInteractor
import com.practicum.playlistmaker.domain.settings.SettingsRepository

class SettingsInteractorImpl(private val settingsRepository: SettingsRepository) :
    SettingsInteractor {
    override fun getTheme(): Boolean {
        return settingsRepository.getStoredTheme()
    }

    override fun setTheme(theme: Boolean) {
        settingsRepository.setTheme(theme)
    }
}