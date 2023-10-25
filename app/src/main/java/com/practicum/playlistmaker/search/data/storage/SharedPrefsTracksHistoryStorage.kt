package com.practicum.playlistmaker.search.data.storage

import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.search.data.TracksHistoryStorage
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.Constants

class SharedPrefsTracksHistoryStorage(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson) :
    TracksHistoryStorage {
    override fun read(): Array<Track> {
        return sharedPreferences.getString(Constants.HISTORY_TRACK_KEY, null)?.let {
            gson.fromJson(it, Array<Track>::class.java)
        } ?: emptyArray()
    }

    override fun saveHistory(tracks: List<Track>) {
        val tracksJson = Gson().toJson(tracks)
        sharedPreferences.edit().putString(Constants.HISTORY_TRACK_KEY, tracksJson).apply()
    }


    override fun clear() {
        sharedPreferences.edit()
            .clear()
            .apply()
    }

}