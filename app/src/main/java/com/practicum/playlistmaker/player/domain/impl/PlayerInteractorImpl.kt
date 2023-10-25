package com.practicum.playlistmaker.player.domain.impl

import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.api.PlayerRepository
import com.practicum.playlistmaker.player.domain.models.PlayerState

class PlayerInteractorImpl(private val trackPlayer: PlayerRepository): PlayerInteractor {

    override fun preparePlayer(url: String) {
        trackPlayer.preparePlayer(url)
    }

    override fun startPlayer(url: String) {
        trackPlayer.startPlayer(url)
    }

    override fun pausePlayer() {
        trackPlayer.pausePlayer()
    }

    override fun releasePlayer() {
        trackPlayer.releasePlayer()
    }

    override fun getCurrentPosition(): Int {
        return trackPlayer.getCurrentPosition()
    }

    override fun getPlayerState(): PlayerState {
        return trackPlayer.playerState
    }

    override fun setTrackCompletionListener(listener: () -> Unit) {
        trackPlayer.setOnCompletionListener(listener)
    }

}