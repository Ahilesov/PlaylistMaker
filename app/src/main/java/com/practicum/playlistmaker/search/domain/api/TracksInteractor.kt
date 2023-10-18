package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.models.Track

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)
    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?, errorMessage: String?)
    }
    fun readSearchHistory(): ArrayList<Track>

    fun addTrackToSearchHistory(track: Track)

    fun clearSearchHistory()

}