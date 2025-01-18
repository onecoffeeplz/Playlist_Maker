package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.LocalRepository
import com.example.playlistmaker.domain.api.LocalRepositoryInteractor

class LocalRepositoryInteractorImpl(private val localRepository: LocalRepository) :
    LocalRepositoryInteractor {
    override fun getBoolean(key: String, defValue: Boolean): Boolean {
        return localRepository.getBoolean(key, defValue)
    }

    override fun putBoolean(key: String, value: Boolean) {
        localRepository.putBoolean(key, value)
    }

    override fun getString(key: String, defValue: String?): String? {
        return localRepository.getString(key, defValue)
    }

    override fun putString(key: String, value: String) {
        localRepository.putString(key, value)
    }

    override fun remove(key: String) {
        localRepository.remove(key)
    }

    override fun contains(key: String): Boolean {
        return localRepository.contains(key)
    }
}