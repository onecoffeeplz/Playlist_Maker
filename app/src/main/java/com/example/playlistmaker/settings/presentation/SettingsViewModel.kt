package com.example.playlistmaker.settings.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.api.AppThemeInteractor
import com.example.playlistmaker.sharing.domain.api.ActionHandlerInteractor

class SettingsViewModel(
    private val appThemeInteractor: AppThemeInteractor,
    private val actionHandler: ActionHandlerInteractor
) : ViewModel() {

    private val _darkThemeEnabled = MutableLiveData<Boolean>()
    val darkThemeEnabled: LiveData<Boolean> get() = _darkThemeEnabled

    init {
        _darkThemeEnabled.value = onGetCurrentTheme()
    }

    private fun onGetCurrentTheme(): Boolean {
        return appThemeInteractor.getCurrentTheme()
    }

    fun onSwitchTheme(isChecked: Boolean) {
        appThemeInteractor.switchTheme(isChecked)
        _darkThemeEnabled.value = isChecked
    }

    fun onShareApp() {
        actionHandler.shareApp()
    }

    fun onContactSupport() {
        actionHandler.contactSupport()
    }

    fun onShowLicense() {
        actionHandler.showLicense()
    }

}