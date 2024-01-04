package com.practicum.playlistmaker.domain.settings.impl

import com.practicum.playlistmaker.domain.settings.SettingRepository
import com.practicum.playlistmaker.domain.settings.SettingInteractor

class SettingInteractorImpl(private val settingRepository: SettingRepository) : SettingInteractor {
    override fun getTheme(): Boolean {
        return settingRepository.getStoredTheme()
    }

    override fun setTheme(theme: Boolean) {
        settingRepository.setTheme(theme)
    }
}