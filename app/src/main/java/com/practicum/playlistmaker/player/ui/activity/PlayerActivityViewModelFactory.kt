package com.practicum.playlistmaker.player.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.Creator
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel

class PlayerActivityViewModelFactory(private val track: Track) : ViewModelProvider.Factory {

    private val playerInteractor = Creator.provideGetPlayerInteractor()

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlayerViewModel(playerInteractor) as T
    }

}