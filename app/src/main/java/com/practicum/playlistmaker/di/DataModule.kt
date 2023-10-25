package com.practicum.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import com.google.gson.Gson
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.TracksHistoryStorage
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.network.TrackApi
import com.practicum.playlistmaker.search.data.storage.SharedPrefsTracksHistoryStorage
import com.practicum.playlistmaker.settings.data.SettingsThemeStorage
import com.practicum.playlistmaker.settings.data.storage.SharedPrefsSettingsThemeStorage
import com.practicum.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import com.practicum.playlistmaker.sharing.domain.api.ExternalNavigator
import com.practicum.playlistmaker.util.Constants
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    factory {
        MediaPlayer()
    }

    single<NetworkClient> {
        RetrofitNetworkClient(
            context = androidContext(),
            iTunesService = get())
    }

    single(named(Constants.HISTORY_TRACK_FILE)) {
        androidContext()
            .getSharedPreferences(Constants.HISTORY_TRACK_FILE, Context.MODE_PRIVATE)
    }

    single(named(Constants.DARK_THEME_SETTINGS)) {
        androidContext()
            .getSharedPreferences(Constants.DARK_THEME_SETTINGS, Context.MODE_PRIVATE)
    }

    single<TrackApi> {
        Retrofit.Builder()
            .baseUrl(Constants.ITUNES_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TrackApi::class.java)
    }

    factory { Gson() }

    single<TracksHistoryStorage> {
        SharedPrefsTracksHistoryStorage(
            sharedPreferences = get(qualifier = named(Constants.HISTORY_TRACK_FILE)),
            gson = get())
    }

    single<SettingsThemeStorage> {
        SharedPrefsSettingsThemeStorage(sharedPreferences = get(qualifier = named(Constants.DARK_THEME_SETTINGS)))
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl(context = androidContext())
    }

}