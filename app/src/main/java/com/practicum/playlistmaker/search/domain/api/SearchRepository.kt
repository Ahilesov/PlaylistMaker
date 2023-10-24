package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.Resource

interface SearchRepository {

    fun searchTracks(expression: String): Resource<List<Track>>

    fun readSearchHistory(): ArrayList<Track>

    fun saveHistory(tracks: List<Track>)

    fun clearSearchHistory()
}