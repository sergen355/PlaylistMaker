package com.practicum.playlistmaker.ui.settings.activity


import android.content.SharedPreferences
import android.os.Bundle
import android.widget.CompoundButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker.App.Companion.PLAYLIST_MAKER_PREFERENCES

import com.practicum.playlistmaker.R

import com.practicum.playlistmaker.ui.settings.view_model.SettingsViewModel


class SettingsActivity : AppCompatActivity() {

    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var themeSwitcher: SwitchMaterial


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        themeSwitcher = findViewById<SwitchMaterial>(R.id.theme_switcher)

        sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)


        settingsViewModel = ViewModelProvider(
            this,
            SettingsViewModel.getViewModelFactory(sharedPrefs, this)
        )[SettingsViewModel::class.java]


        settingsViewModel.currentTheme.observe(this) { currentTheme ->
            themeSwitcher.isChecked = currentTheme
        }

        themeSwitcher.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            settingsViewModel.setTheme(isChecked)
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
            )
        })

        val back = findViewById<ImageView>(R.id.back)
        back.setOnClickListener {
            this.finish()
        }

        val shareView = findViewById<ImageView>(R.id.share)
        shareView.setOnClickListener {
            settingsViewModel.shareLink()
        }
        val supportView = findViewById<ImageView>(R.id.support)
        supportView.setOnClickListener {
            settingsViewModel.sendSupport()
        }

        val eulaView = findViewById<ImageView>(R.id.eula)
        eulaView.setOnClickListener {
            settingsViewModel.openLink()
        }
    }

}
