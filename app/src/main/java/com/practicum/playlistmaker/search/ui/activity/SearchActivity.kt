package com.practicum.playlistmaker.search.ui.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.practicum.playlistmaker.util.Constants
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.player.ui.activity.PlayerActivity
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import com.practicum.playlistmaker.search.ui.view_model.SearchState
import com.practicum.playlistmaker.search.ui.view_model.SearchStatus
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var trackHistoryAdapter: TrackAdapter

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var binding: ActivitySearchBinding
    private val viewModel by viewModel<SearchViewModel>()

    private var searchText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.searchScreenState.observe(this) {state ->
            render(state)
        }

        setupAdapters()
        setupListeners()

        // отслеживания состояния фокуса
        binding.inputEditText.setOnFocusChangeListener { view, hasFocus ->
            viewModel.searchFocusChanged(hasFocus, binding.inputEditText.text.toString())
        }

        binding.inputEditText.addTextChangedListener(object :
            TextWatcher { // ввод текста в строку поиска
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // реализация позже
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.ivClearIcon.visibility = clearButtonVisibility(s)
                searchText = s?.toString() ?: ""
                if (binding.inputEditText.hasFocus() && searchText.isEmpty()) {
                    viewModel.clearSearchLine()
                }

                viewModel.searchDebounce(searchText)

            }

            override fun afterTextChanged(s: Editable?) {
                // реализация позже
            }
        })
    }

    private fun setupAdapters() {
        // инициализация адптера поиска с добавление трека в историю и открытием активити плеера
        trackAdapter = TrackAdapter() {
            viewModel.addTrackToSearchHistory(it)
            intentAudioPlayer(it)
        }

        binding.rvTrack.adapter = trackAdapter

        // инициализация адптера истории с открытием активити плеера
        trackHistoryAdapter = TrackAdapter() {
            intentAudioPlayer(it)
        }

        binding.rvHistory.adapter = trackHistoryAdapter
    }

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Loading -> setStatus(SearchStatus.PROGRESS)

            is SearchState.Content -> {
                setStatus(SearchStatus.SUCCESS)
                trackAdapter.setTracks(state.tracks)
            }

            is SearchState.SearchHistory -> {
                trackHistoryAdapter.setTracks(state.tracks)
                if(state.tracks.isNotEmpty()) {
                    setStatus(SearchStatus.HISTORY)
                } else {
                    setStatus(SearchStatus.ALL_GONE)
                }
            }

            is SearchState.Empty -> {
                setStatus(SearchStatus.EMPTY_SEARCH)
            }

            is SearchState.Error -> {
                setStatus(SearchStatus.CONNECTION_ERROR)
            }

        }
    }

    private fun setStatus(status: SearchStatus) {
        when (status) {
            SearchStatus.PROGRESS -> {
                binding.rvTrack.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
                binding.llErrorConnection.visibility = View.GONE
                binding.llErrorNotFound.visibility = View.GONE
                binding.llHistory.visibility = View.GONE
            }

            SearchStatus.CONNECTION_ERROR -> {
                binding.rvTrack.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
                binding.llErrorConnection.visibility = View.VISIBLE
                binding.llErrorNotFound.visibility = View.GONE
                binding.llHistory.visibility = View.GONE
            }

            SearchStatus.EMPTY_SEARCH -> {
                binding.rvTrack.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
                binding.llErrorConnection.visibility = View.GONE
                binding.llErrorNotFound.visibility = View.VISIBLE
                binding.llHistory.visibility = View.GONE
            }

            SearchStatus.SUCCESS -> {
                binding.rvTrack.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
                binding.llErrorConnection.visibility = View.GONE
                binding.llErrorNotFound.visibility = View.GONE
                binding.llHistory.visibility = View.GONE
            }

            SearchStatus.HISTORY -> {
                binding.rvTrack.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
                binding.llErrorConnection.visibility = View.GONE
                binding.llErrorNotFound.visibility = View.GONE
                binding.llHistory.visibility = View.VISIBLE
            }

            SearchStatus.ALL_GONE -> {
                binding.rvTrack.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
                binding.llErrorConnection.visibility = View.GONE
                binding.llErrorNotFound.visibility = View.GONE
                binding.llHistory.visibility = View.GONE
            }
        }
    }

    // функция для доступа кнопки через задержку Handler
    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, Constants.CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    // функция явного Intent c передачей объекта Track с проверкой доступности кнопки
    private fun intentAudioPlayer(track: Track) {
        if (clickDebounce()) {
            val displayIntent = Intent(this, PlayerActivity::class.java)
            displayIntent.putExtra(Constants.TRACK, track)
            startActivity(displayIntent)
        }
    }

    // функция показа крестика поиска
    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun setupListeners() {
        // кнопка назад
        binding.ivBack.setOnClickListener { finish() }
        // кнопка обновить
        binding.buttonRefresh.setOnClickListener {
            viewModel.searchDebounce(binding.inputEditText.text.toString())
        }

        // кнопка очистить в истории поиска
        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearSearchHistory()
        }

        // кнопка крестик в строке поиска
        binding.ivClearIcon.setOnClickListener {
            binding.inputEditText.setText("")
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager // прячем клавиатуру с нажатием на крестик
            inputMethodManager?.hideSoftInputFromWindow(binding.inputEditText.windowToken, 0)
            viewModel.clearSearchLine()
        }
    }

    // сохраняем данные
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val searchText = binding.inputEditText.text.toString()
        outState.putString(Constants.SEARCH_TEXT, searchText)
    }

    // показываем сохраненные данные
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val searchText = savedInstanceState.getString(Constants.SEARCH_TEXT)
        binding.inputEditText.setText(searchText)
    }

}



