package com.practicum.playlistmaker.ui.settings.view_model

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.domain.settings.SettingInteractor
import com.practicum.playlistmaker.domain.sharing.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingInteractor: SettingInteractor,
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
        _currentThemeMutable.value = settingInteractor.getTheme()
    }

    fun setTheme(theme: Boolean) {
        settingInteractor.setTheme(theme)
        _currentThemeMutable.value = theme
    }

    companion object {
        fun getViewModelFactory(
            sharedPreferences: SharedPreferences,
            context: Context
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SettingsViewModel(
                        Creator.provideSharingInteractor(context),
                        Creator.provideSettingInteractor(sharedPreferences)
                    ) as T
                }
            }
    }

}