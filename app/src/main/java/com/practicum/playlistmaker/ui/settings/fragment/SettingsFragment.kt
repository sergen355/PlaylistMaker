package com.practicum.playlistmaker.ui.settings.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import com.practicum.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    private val binding get() = _binding!!
    private val settingsViewModel by viewModel<SettingsViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.themeSwitcher.isUseMaterialThemeColors = false
        binding.themeSwitcher.thumbTintList = ContextCompat.getColorStateList(view.context, R.color.thumb_selector)
        binding.themeSwitcher.trackTintList = ContextCompat.getColorStateList(view.context, R.color.track_selector)

        settingsViewModel.currentTheme.observe(viewLifecycleOwner) { currentTheme ->
            binding.themeSwitcher.isChecked = currentTheme
        }

        binding.themeSwitcher.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            settingsViewModel.setTheme(isChecked)
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
            )
        })

        binding.share.setOnClickListener {
            settingsViewModel.shareLink()
        }

        binding.support.setOnClickListener {
            settingsViewModel.sendSupport()
        }

        binding.eula.setOnClickListener {
            settingsViewModel.openLink()
        }
    }
}