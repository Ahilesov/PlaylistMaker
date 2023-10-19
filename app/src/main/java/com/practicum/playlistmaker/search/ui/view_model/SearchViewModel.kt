package com.practicum.playlistmaker.search.ui.view_model

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.Constants

class SearchViewModel(
    private val searchInteractor: SearchInteractor
) : ViewModel() {

    companion object {
        private val SEARCH_REQUEST_TOKEN = Any()
    }

    private val historyList = ArrayList<Track>()

    private var _searchScreenState = MutableLiveData<SearchState>()
    var searchScreenState: LiveData<SearchState> = _searchScreenState

    init {
        historyList.addAll(searchInteractor.readSearchHistory())
    }

    private val handler = Handler(Looper.getMainLooper())

    fun searchDebounce(changedText: String) {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        val searchRunnable = Runnable { searchRequest(changedText) }
        val postTime = SystemClock.uptimeMillis() + Constants.SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }

    private fun searchRequest(newSearchText: String) {

        if (newSearchText.isNotEmpty()) {
            _searchScreenState.value = SearchState.Loading

            searchInteractor.searchTracks(newSearchText, object : SearchInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                    val tracks = mutableListOf<Track>()
                    if (foundTracks != null) {
                        tracks.addAll(foundTracks)
                    }

                    when {
                        errorMessage != null -> {
                            _searchScreenState.postValue(SearchState.Error(
                                errorMessage = errorMessage)
                            )
                        }

                        tracks.isEmpty() -> {
                            _searchScreenState.postValue(SearchState.Empty(
                                emptyMessage = "Ничего не нашлось")
                            )
                        }

                        else -> {
                            _searchScreenState.postValue(SearchState.Content(
                                tracks = tracks)
                            )
                        }
                    }
                }
            })
        }
    }

    fun clearSearchHistory() {
        historyList.clear()
        _searchScreenState.value = SearchState.SearchHistory(historyList)
        searchInteractor.clearSearchHistory()
    }

    fun searchFocusChanged(hasFocus: Boolean, text: String) {
        if (hasFocus && text.isEmpty()) {
            _searchScreenState.value = SearchState.SearchHistory(historyList)
        }
    }

    fun clearSearchLine() {
        _searchScreenState.value = SearchState.SearchHistory(historyList)
    }

    fun addTrackToSearchHistory(track: Track) {
        if (historyList.contains(track)) {
            historyList.remove(track)
        }
        historyList.add(0, track)
        if (historyList.size > Constants.MAX_HISTORY_SIZE) {
            historyList.removeLast()
        }
        searchInteractor.saveHistory(historyList)
    }

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

}