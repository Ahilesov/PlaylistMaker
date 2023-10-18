package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.search.domain.models.Track

interface TracksHistoryStorage {
    fun read(): Array<Track>
    fun add(track: Track)
    fun clear()

}