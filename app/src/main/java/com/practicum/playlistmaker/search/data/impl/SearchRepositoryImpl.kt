package com.practicum.playlistmaker.search.data.impl

import android.util.Log
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.TracksHistoryStorage
import com.practicum.playlistmaker.search.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.search.data.dto.TracksSearchResponse
import com.practicum.playlistmaker.search.domain.api.SearchRepository
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.Resource

class SearchRepositoryImpl(
    private val networkClient: NetworkClient,
    private val tracksHistoryStorage: TracksHistoryStorage
) : SearchRepository {
    override fun searchTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        return when (response.resultCode) {
            -1 -> {
                Resource.Error("Проверьте подключение к интернету")
            }

            200 -> {
                Resource.Success((response as TracksSearchResponse).results.map {
                    Track(
                        it.trackId,
                        it.trackName,
                        it.artistName,
                        it.trackTimeMillis,
                        it.artworkUrl100,
                        it.collectionName,
                        it.releaseDate,
                        it.primaryGenreName,
                        it.country,
                        it.previewUrl
                    )
                })
            }

            else -> {
                Resource.Error("Ошибка сервера")
            }
        }
    }

    override fun readSearchHistory(): ArrayList<Track> {
        return tracksHistoryStorage.read().toCollection(ArrayList())
    }

    override fun saveHistory(tracks: List<Track>) {
        tracksHistoryStorage.saveHistory(tracks)
    }


    override fun clearSearchHistory() {
        tracksHistoryStorage.clear()
    }
}