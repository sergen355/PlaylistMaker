package com.practicum.playlistmaker.ui.settings.view_model


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.practicum.playlistmaker.domain.settings.SettingsInteractor
import com.practicum.playlistmaker.domain.sharing.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
) : ViewModel() {
    private var _currentThemeMutable = MutableLiveData<Boolean>()
    val currentTheme: LiveData<Boolean> = _currentThemeMutable


    init {
        getTheme()
    }


    fun shareLink() {
        sharingInteractor.shareApp()
    }

    fun openLink() {
        sharingInteractor.openTerms()
    }

    fun sendSupport() {
        sharingInteractor.openSupport()
    }

    fun getTheme() {
        _currentThemeMutable.value = settingsInteractor.getTheme()
    }

    fun setTheme(theme: Boolean) {
        settingsInteractor.setTheme(theme)
        _currentThemeMutable.value = theme
    }

}