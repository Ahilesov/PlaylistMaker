package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.player.data.TrackPlayerImpl
import com.practicum.playlistmaker.player.domain.api.TrackPlayer
import com.practicum.playlistmaker.search.data.impl.SearchRepositoryImpl
import com.practicum.playlistmaker.search.domain.api.SearchRepository
import com.practicum.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository
import org.koin.dsl.module


val repositoryModule = module {

    single<TrackPlayer> {
        TrackPlayerImpl()
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