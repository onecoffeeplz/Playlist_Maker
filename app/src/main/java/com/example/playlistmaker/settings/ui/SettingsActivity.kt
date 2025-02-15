package com.example.playlistmaker.settings.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.settings.presentation.SettingsViewModel

class SettingsActivity : AppCompatActivity() {

    private var _binding: ActivitySettingsBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for ActivitySettingsBinding must not be null!")

    private val viewModel by viewModels<SettingsViewModel> { SettingsViewModel.getViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.darkThemeEnabled.observe(this) { isDarkThemeEnabled ->
            binding.settingsTheme.isChecked = isDarkThemeEnabled
        }

        with(binding) {
            settingsToolbar.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
            settingsShare.setOnClickListener {
                viewModel.onShareApp()
            }
            settingsSupport.setOnClickListener {
                viewModel.onContactSupport()
            }
            settingsLicense.setOnClickListener {
                viewModel.onShowLicense()
            }
        }

        binding.settingsTheme.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onSwitchTheme(isChecked)
        }
    }
}