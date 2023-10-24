package com.practicum.playlistmaker.player.ui.view_model


import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.util.Constants
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.models.PlayerState
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
) : ViewModel() {

    private val _playState = MutableLiveData<Boolean>()
    val playState: LiveData<Boolean> = _playState

    private val _playCountdown = MutableLiveData<String>()
    val playCountdown: LiveData<String> = _playCountdown

    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            _playCountdown.postValue(
                SimpleDateFormat("mm:ss", Locale.getDefault())
                    .format(playerInteractor.getCurrentPosition())
            )
            handler.postDelayed(this, Constants.TRACK_TIMER_DEBOUNCE_DELAY)
        }
    }

    private fun startPlayer(url: String) {
        playerInteractor.startPlayer(url)
        handler.post(runnable)
        _playState.value = true
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer()
        handler.removeCallbacks(runnable)
        _playState.value = false
    }

    override fun onCleared() {
        super.onCleared()
        playerInteractor.releasePlayer()
        _playState.value = false
        handler.removeCallbacks(runnable)
    }

    fun playbackControl(url: String) {
        when (playerInteractor.getPlayerState()) {
            PlayerState.PLAYING -> {
                pausePlayer()
            }

            PlayerState.PAUSED, PlayerState.PREPARED -> {
                startPlayer(url)
            }

            PlayerState.DEFAULT -> {
                startPlayer(url)
                playerInteractor.setTrackCompletionListener {
                    _playState.value = false
                    _playCountdown.value = Constants.TRACK_COUNTDOWN_TEXT
                    handler.removeCallbacks(runnable)
                }
            }
        }
    }



}