package com.practicum.playlistmaker.search.ui.activity

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import com.practicum.playlistmaker.util.Creator

class SearchViewModelFactory(context: Context): ViewModelProvider.Factory {

    private val tracksInteractor = Creator.provideTracksInteractor(context = context)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchViewModel(
            tracksInteractor = tracksInteractor
        ) as T
    }
}