package com.example.playlistmaker.settings.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.settings.presentation.SettingsViewModel

class SettingsActivity : AppCompatActivity() {

    private var _binding: ActivitySettingsBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for ActivitySettingsBinding must not be null!")

    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            SettingsViewModel.getViewModelFactory()
        )[SettingsViewModel::class.java]
        viewModel.darkThemeEnabled().observe(this) {
            binding.settingsTheme.isChecked = it
        }

        with (binding) {
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

        with (binding.settingsTheme) {
            isChecked = viewModel.onGetCurrentTheme()
            setOnCheckedChangeListener { _, isChecked ->
                viewModel.onSwitchTheme(isChecked)
            }
        }
    }
}