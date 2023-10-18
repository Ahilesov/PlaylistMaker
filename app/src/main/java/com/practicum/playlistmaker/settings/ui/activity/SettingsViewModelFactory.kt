package com.practicum.playlistmaker.settings.ui.activity

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.practicum.playlistmaker.util.Creator

class SettingsViewModelFactory(context: Context, application: Application): ViewModelProvider.AndroidViewModelFactory(application) {

    private val settingsInteractor = Creator.provideSettingsInteractor(context)
    private val sharingInteractor = Creator.provideSharingInteractor(context)
    private val getApplication = application as App

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingsViewModel(
            settingsInteractor = settingsInteractor,
            sharingInteractor = sharingInteractor,
            application = getApplication
        ) as T
    }
}