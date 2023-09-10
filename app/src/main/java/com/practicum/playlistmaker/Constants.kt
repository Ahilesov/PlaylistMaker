package com.practicum.playlistmaker

object Constants {
    const val TRACK = "track"
    const val SEARCH_TEXT = "SEARCH_TEXT"
    const val ITUNES_BASE_URL = "https://itunes.apple.com"
    const val HISTORY_TRACK_FILE = "history_track_file"
    const val HISTORY_TRACK_KEY = "key_for_history_track"
    const val SEARCH_DEBOUNCE_DELAY = 2000L
    const val CLICK_DEBOUNCE_DELAY = 1000L
    const val TRACK_TIMER_DEBOUNCE_DELAY = 500L
    const val STATE_DEFAULT_PLAYER = 0
    const val STATE_PREPARED_PLAYER = 1
    const val STATE_PLAYING_PLAYER = 2
    const val STATE_PAUSED_PLAYER = 3
}