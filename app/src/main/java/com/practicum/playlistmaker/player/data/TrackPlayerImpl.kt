package com.practicum.playlistmaker.player.data

import android.media.MediaPlayer
import android.util.Log
import com.practicum.playlistmaker.player.domain.api.TrackPlayer
import com.practicum.playlistmaker.player.domain.models.PlayerState

class TrackPlayerImpl : TrackPlayer {

    override var playerState: PlayerState = PlayerState.DEFAULT
    private var mediaPlayer = MediaPlayer()

    override fun preparePlayer(url: String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepare()
        playerState = PlayerState.PREPARED
    }

    override fun startPlayer(url: String) {
        if (playerState == PlayerState.DEFAULT) {
            preparePlayer(url)
        }
        mediaPlayer.start()
        playerState = PlayerState.PLAYING
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
        playerState = PlayerState.PAUSED
    }

    override fun releasePlayer() {
//        if(playerState !== PlayerState.DEFAULT) {
//            Log.e("AAA", "release != $playerState")
//            mediaPlayer.stop()
//            mediaPlayer.reset()
//            mediaPlayer.release()
//            playerState = PlayerState.DEFAULT
//            Log.e("AAA", "release = $playerState")
//        }
        mediaPlayer.release()
        playerState = PlayerState.DEFAULT
        Log.e("AAA", "release = $playerState")

    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun setOnCompletionListener(listener: () -> Unit) {
        mediaPlayer.setOnCompletionListener {
            playerState = PlayerState.PREPARED
            listener.invoke()
        }
    }

}