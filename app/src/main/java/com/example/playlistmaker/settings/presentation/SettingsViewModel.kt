package com.example.playlistmaker.settings.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.settings.domain.api.AppThemeInteractor
import com.example.playlistmaker.sharing.domain.api.ActionHandlerInteractor

class SettingsViewModel(
    private val appThemeInteractor: AppThemeInteractor,
    private val actionHandler: ActionHandlerInteractor
) : ViewModel() {

    private val _darkThemeEnabled = MutableLiveData<Boolean>()
    fun darkThemeEnabled(): LiveData<Boolean> = _darkThemeEnabled

    init {
        onSwitchTheme(onGetCurrentTheme())
    }

    fun onGetCurrentTheme(): Boolean {
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

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val appThemeInteractor = Creator.provideAppThemeInteractor()
                val actionHandler = Creator.provideActionHandlerInteractor()
                SettingsViewModel(appThemeInteractor, actionHandler)
            }
        }
    }
}