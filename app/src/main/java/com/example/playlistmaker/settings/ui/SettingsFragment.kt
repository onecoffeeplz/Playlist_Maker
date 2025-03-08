package com.example.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.settings.presentation.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentSettingsBinding must not be null!")

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.darkThemeEnabled.observe(viewLifecycleOwner) { isDarkThemeEnabled ->
            binding.settingsTheme.isChecked = isDarkThemeEnabled
        }

        with(binding) {
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