package com.practicum.playlistmaker.data.settings.impl

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.App.Companion.DARK_THEME_KEY
import com.practicum.playlistmaker.App.Companion.PLAYLIST_MAKER_PREFERENCES
import com.practicum.playlistmaker.domain.settings.SettingsRepository

class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {
    private val sharedPreferences =
        context.getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, AppCompatActivity.MODE_PRIVATE)

    override fun getStoredTheme(): Boolean {
        return sharedPreferences.getBoolean(DARK_THEME_KEY, false)

    }

    override fun setTheme(theme: Boolean) {
        sharedPreferences.edit()
            .putBoolean(DARK_THEME_KEY, theme)
            .apply()
    }
}