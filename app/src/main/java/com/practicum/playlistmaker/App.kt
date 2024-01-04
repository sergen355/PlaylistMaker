package com.practicum.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.creator.Creator

class App : Application() {

    companion object {
        const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
        const val DARK_THEME_KEY = "dark_theme"
    }

    var darkTheme = false
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
        val settingInteractor = Creator.provideSettingInteractor(sharedPrefs)

        darkTheme = settingInteractor.getTheme()

        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        sharedPrefs.edit().putBoolean(DARK_THEME_KEY, darkThemeEnabled).apply()
    }
}