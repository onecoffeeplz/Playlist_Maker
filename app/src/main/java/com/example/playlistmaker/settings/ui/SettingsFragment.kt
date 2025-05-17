package com.example.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.settings.presentation.SettingsViewModel
import dev.androidbroadcast.vbpd.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private val binding by viewBinding(FragmentSettingsBinding::bind)

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentSettingsBinding.inflate(inflater, container, false).root
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