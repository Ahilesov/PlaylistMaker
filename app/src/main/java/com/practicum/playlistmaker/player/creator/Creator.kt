package com.practicum.playlistmaker.player.creator

import com.practicum.playlistmaker.player.data.TrackPlayerImpl
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.api.TrackPlayer
import com.practicum.playlistmaker.player.domain.impl.PlayerInteractorImpl

object Creator {

    fun provideGetPlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(provideTrackPlayer())
    }

    private fun provideTrackPlayer(): TrackPlayer{
        return TrackPlayerImpl()
    }
}