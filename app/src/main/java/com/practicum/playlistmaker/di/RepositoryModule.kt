package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.player.data.PlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.api.PlayerRepository
import com.practicum.playlistmaker.search.data.impl.SearchRepositoryImpl
import com.practicum.playlistmaker.search.domain.api.SearchRepository
import com.practicum.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository
import org.koin.dsl.module


val repositoryModule = module {

    factory<PlayerRepository> {
        PlayerRepositoryImpl(mediaPlayer = get())
    }

    single<SearchRepository> {
        SearchRepositoryImpl(
            networkClient = get(),
            tracksHistoryStorage = get()
        )
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(storage = get())
    }

}