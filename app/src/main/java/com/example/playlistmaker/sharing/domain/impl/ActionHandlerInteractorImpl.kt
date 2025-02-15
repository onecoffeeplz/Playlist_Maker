package com.example.playlistmaker.sharing.domain.impl

import com.example.playlistmaker.sharing.domain.api.ActionHandlerInteractor
import com.example.playlistmaker.sharing.domain.api.ActionHandlerRepository

class ActionHandlerInteractorImpl(private val action: ActionHandlerRepository):
    ActionHandlerInteractor {
    override fun shareApp() {
        action.shareApp()
    }

    override fun contactSupport() {
        action.contactSupport()
    }

    override fun showLicense() {
        action.showLicense()
    }
}