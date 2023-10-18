package com.practicum.playlistmaker.search.ui.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.util.Constants
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.player.ui.activity.PlayerActivity
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import com.practicum.playlistmaker.search.ui.view_model.SearchState
import com.practicum.playlistmaker.search.ui.view_model.SearchStatus

class SearchActivity : AppCompatActivity() {

    private lateinit var adapterTrack: TrackAdapter
    private lateinit var searchAdapterTrack: TrackAdapter

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    private var trackHistoryList = ArrayList<Track>()

    private lateinit var binding: ActivitySearchBinding
    private lateinit var viewModel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, SearchViewModelFactory(this))
            .get(SearchViewModel::class.java)

        viewModel.searchScreenState.observe(this) {
            render(it)
        }

        viewModel.historyList.observe(this) {
            trackHistoryList = it
        }

        setupAdapters()
        setupListeners()

        // отслеживания состояния фокуса
        binding.inputEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus
                && binding.inputEditText.text.isEmpty()
                && trackHistoryList.isNotEmpty()
            ) {
                updateTrackHistoryList()
                setStatus(SearchStatus.HISTORY)
            } else {
                setStatus(SearchStatus.ALL_GONE)
            }
        }

        binding.inputEditText.addTextChangedListener(object :
            TextWatcher { // ввод текста в строку поиска
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // реализация позже
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.ivClearIcon.visibility = clearButtonVisibility(s)

                if (binding.inputEditText.hasFocus() && s!!.isEmpty()) {
                    updateTrackHistoryList()
                    setStatus(SearchStatus.HISTORY)
                } else {
                    setStatus(SearchStatus.ALL_GONE)
                }

                viewModel.searchDebounce(s?.toString() ?: "")

            }

            override fun afterTextChanged(s: Editable?) {
                // реализация позже
            }
        })
    }

    private fun setupAdapters() {
        // инициализация адптера поиска с добавление трека в историю и открытием активити плеера
        adapterTrack = TrackAdapter() {
            viewModel.addTrackToSearchHistory(it)
            intentAudioPlayer(it)
        }

        binding.rvTrack.adapter = adapterTrack

        // инициализация адптера истории с открытием активити плеера
        searchAdapterTrack = TrackAdapter() {
            intentAudioPlayer(it)
        }

        binding.rvHistory.adapter = searchAdapterTrack
        updateTrackHistoryList()
    }

    private fun updateTrackHistoryList() {
        viewModel.getTracksFromSearchHistory()
        searchAdapterTrack.listTrack = trackHistoryList
        searchAdapterTrack.notifyDataSetChanged()
    }

    private fun render(it: SearchState) {
        Log.e("AAA", "status = $it")
        when (it) {
            is SearchState.Loading -> setStatus(SearchStatus.PROGRESS)

            is SearchState.Content -> {
                setStatus(SearchStatus.SUCCESS)
                adapterTrack.listTrack.clear()
                adapterTrack.listTrack.addAll(it.tracks)
                adapterTrack.notifyDataSetChanged()
            }

            is SearchState.Empty -> {
                setStatus(SearchStatus.EMPTY_SEARCH)
                adapterTrack.listTrack.clear()
                adapterTrack.notifyDataSetChanged()
            }

            is SearchState.Error -> {
                setStatus(SearchStatus.CONNECTION_ERROR)
                adapterTrack.listTrack.clear()
                adapterTrack.notifyDataSetChanged()
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
            setStatus(SearchStatus.ALL_GONE)
        }

        // кнопка крестик в строке поиска
        binding.ivClearIcon.setOnClickListener {
            binding.inputEditText.setText("")
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager // прячем клавиатуру с нажатием на крестик
            inputMethodManager?.hideSoftInputFromWindow(binding.inputEditText.windowToken, 0)

            updateTrackHistoryList()
            if (trackHistoryList.isNotEmpty()) setStatus(SearchStatus.HISTORY) else setStatus(
                SearchStatus.ALL_GONE
            )
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



