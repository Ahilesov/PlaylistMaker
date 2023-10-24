package com.practicum.playlistmaker.di

import android.util.Log
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel<PlayerViewModel> {
        PlayerViewModel(playerInteractor = get())
    }

    viewModel<SearchViewModel> {
        SearchViewModel(searchInteractor = get())
    }

    viewModel<SettingsViewModel> {
        SettingsViewModel(
            settingsInteractor = get(),
            sharingInteractor = get(),
            application = androidApplication() as App
        )
    }


}