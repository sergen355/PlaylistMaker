package com.practicum.playlistmaker.ui.settings.activity

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.google.android.material.switchmaterial.SwitchMaterial

import com.practicum.playlistmaker.R

import com.practicum.playlistmaker.ui.settings.view_model.SettingsViewModel

import org.koin.androidx.viewmodel.ext.android.viewModel


class SettingsActivity : AppCompatActivity() {

    private val settingsViewModel by viewModel<SettingsViewModel>()

    private lateinit var themeSwitcher: SwitchMaterial


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        themeSwitcher = findViewById<SwitchMaterial>(R.id.theme_switcher)
        themeSwitcher.isUseMaterialThemeColors = false
        themeSwitcher.thumbTintList = ContextCompat.getColorStateList(this, R.color.thumb_selector)
        themeSwitcher.trackTintList = ContextCompat.getColorStateList(this, R.color.track_selector)


        settingsViewModel.currentTheme.observe(this) { currentTheme ->
            themeSwitcher.isChecked = currentTheme
        }

        themeSwitcher.setOnCheckedChangeListener { buttonView, isChecked ->
            settingsViewModel.setTheme(isChecked)
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
            )
        }

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
