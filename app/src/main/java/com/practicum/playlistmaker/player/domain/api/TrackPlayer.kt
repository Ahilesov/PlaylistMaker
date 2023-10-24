package com.practicum.playlistmaker.player.domain.api

import com.practicum.playlistmaker.player.domain.models.PlayerState

interface TrackPlayer {
    var playerState: PlayerState
    fun preparePlayer(url: String)
    fun startPlayer(url: String)
    fun pausePlayer()
    fun releasePlayer()
    fun getCurrentPosition(): Int
    fun setOnCompletionListener(listener: (() -> Unit))
}