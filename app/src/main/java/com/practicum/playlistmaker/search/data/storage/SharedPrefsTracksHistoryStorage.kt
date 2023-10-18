package com.practicum.playlistmaker.search.data.storage

import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.search.data.TracksHistoryStorage
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.Constants

class SharedPrefsTracksHistoryStorage(private val sharedPreferences: SharedPreferences) :
    TracksHistoryStorage {
    override fun read(): Array<Track> {
        return sharedPreferences.getString(Constants.HISTORY_TRACK_KEY, null)?.let {
            Gson().fromJson(it, Array<Track>::class.java)
        } ?: emptyArray()
    }

    override fun add(track: Track) {
        val searchHistoryTrackList = read().toMutableList()
        if (searchHistoryTrackList.contains(track)) {
            searchHistoryTrackList.remove(track)
        }
        searchHistoryTrackList.add(0, track)

        if (searchHistoryTrackList.size > 10) {
            searchHistoryTrackList.removeLast()
        }

        sharedPreferences.edit()
            .putString(Constants.HISTORY_TRACK_KEY, Gson().toJson(searchHistoryTrackList))
            .apply()

    }

    override fun clear() {
        sharedPreferences.edit()
            .clear()
            .apply()
    }

}