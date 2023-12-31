package searchtrack

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.AudioPlayerActivity
import com.practicum.playlistmaker.Constants
import com.practicum.playlistmaker.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private lateinit var inputEditText: EditText
    private lateinit var ivBackButton: ImageView
    private lateinit var ivClearButton: ImageView
    private lateinit var rvTrack: RecyclerView
    private lateinit var rvHistory: RecyclerView
    private lateinit var llErrorNothingWasFound: LinearLayout
    private lateinit var llEerrorConnection: LinearLayout
    private lateinit var butRefresh: Button
    private lateinit var butClearHistory: Button
    private lateinit var adapterTrack: TrackAdapter
    private lateinit var searchAdapterTrack: TrackAdapter
    private val trackList = ArrayList<Track>()
    private lateinit var searchTrackList: Array<Track>
    private lateinit var llHistory: LinearLayout
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var searchHistory: SearchHistory
    private lateinit var progressBar: ProgressBar

    /////////
    private val iTunesBaseUrl = Constants.ITUNES_BASE_URL
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(TrackApi::class.java)


    // переменная для минимизации рисков открытия нескольких аудиоплееров одновременно
    private var isClickAllowed = true

    // создание экземпляря Handler главного потока
    private val handler = Handler(Looper.getMainLooper())

    private val searchRunnable = Runnable { search() }
    /////////


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        ///////// ИНИЦИАЛИЗАЦИЯ
        ivBackButton = findViewById(R.id.ivBack)
        inputEditText = findViewById(R.id.inputEditText)
        ivClearButton = findViewById(R.id.ivClearIcon)
        butClearHistory = findViewById(R.id.clearHistoryButton)
        rvTrack = findViewById(R.id.rvTrack)
        llErrorNothingWasFound = findViewById(R.id.llErrorNotFound)
        llEerrorConnection = findViewById(R.id.llErrorConnection)
        butRefresh = findViewById(R.id.buttonRefresh)
        llHistory = findViewById(R.id.llHistory)
        rvHistory = findViewById(R.id.rvHistory)
        progressBar = findViewById(R.id.progressBar)

        // инициализация адптера поиска с добавление трека в историю и открытием активити плеера
        adapterTrack = TrackAdapter() {
            searchHistory.write(it)
            intentAudioPlayer(it)
        }

        // инициализация адптера истории с открытием активити плеера
        searchAdapterTrack = TrackAdapter() {
            intentAudioPlayer(it)
        }
        /////////

        ///////// СОЗДАНИЕ ЭКЗЕМПЛЯРА sharedPreferences
        // создаем экземпляр sharedPreferences
        sharedPreferences = getSharedPreferences(Constants.HISTORY_TRACK_FILE, MODE_PRIVATE)
        // создаем экземпляр SearchHistory
        searchHistory = SearchHistory(sharedPreferences)
        /////////

        ///////// ВЕШАЕМ СЛУШАТЕЛИ НА КНПОПКИ НАЗАД И ОБНОВИТЬ
        ivBackButton.setOnClickListener { finish() } // кнопка назад
        butRefresh.setOnClickListener { search() } // кнопка обновить
        /////////

        ///////// ВЕШАЕМ СЛУШАТЕЛЬ НА КРЕСТИК В СТРОКЕ ПОИСКА
        ivClearButton.setOnClickListener {
            inputEditText.setText("")
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager // прячем клавиатуру с нажатием на крестик
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
            trackList.clear()
            adapterTrack.notifyDataSetChanged()

            // ВЫЗЫВАЕМ ФУНКЦИЯ ПОКАЗА ИСТОРИИ ЛИСТА
            showHistoryList()
            searchAdapterTrack.notifyDataSetChanged()
            if (searchTrackList.isEmpty()) {
                llErrorNothingWasFound.visibility = View.GONE
                llEerrorConnection.visibility = View.GONE
                llHistory.visibility = View.GONE
                rvTrack.visibility = View.GONE
            } else {
                llHistory.visibility = View.VISIBLE
                llErrorNothingWasFound.visibility = View.GONE
                llEerrorConnection.visibility = View.GONE
                rvTrack.visibility = View.GONE
            }
        }

        ///////// ВЕШАЕМ СЛУШАТЕЛЯ НА КНОПКУ ОЧИСТИТЬ СПИСОК В ИСТОРИИ ПОИСКА
        butClearHistory.setOnClickListener {
            searchHistory.clear()
            llHistory.visibility = View.GONE
            searchTrackList = emptyArray()
            rvHistory.adapter?.notifyDataSetChanged()
        }
        /////////

        /////////СЛУШАТЕЛЬ ДЛЯ ОТСЛЕЖИВАНИЯ СОСТОЯНИЯ ФОКУСА
        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            llHistory.visibility = if (
                hasFocus
                && inputEditText.text.isEmpty()
                && readTrackHistoryList().isNotEmpty()
            ) View.VISIBLE else View.GONE
        }

        inputEditText.addTextChangedListener(object : TextWatcher { // ввод текста в строку поиска
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // реализация позже
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                ivClearButton.visibility = clearButtonVisibility(s)
                llHistory.visibility =
                    if (inputEditText.hasFocus() && s!!.isEmpty()) View.VISIBLE else View.GONE
                searchDebounce()
            }

            override fun afterTextChanged(s: Editable?) {
                // реализация позже
            }
        })

        ///////// ИНИЦИАЛИЗИРУЕМ listTrack АДАПТЕРА ТРЕКОВ И ПОМЕЩАЕМ В Recycler ТРЕКОВ ЭТОТ ЛИСТ
        adapterTrack.listTrack = trackList
        rvTrack.adapter = adapterTrack

        ///////// ВЫЗЫВАЕМ ФУНКЦИЯ ПОКАЗА ИСТОРИИ ЛИСТА
        showHistoryList()
    }

    ///////// ФУНКЦИЯ удаляет запланированный вызов Runnable и запуск его же через 2 сек
    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, Constants.SEARCH_DEBOUNCE_DELAY)
    }

    ///////// ФУНКЦИЯ ДЛЯ ДОСТУПА КНОПКИ ЧЕРЕЗ ЗАДЕРЖКУ Handler
    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({isClickAllowed = true}, Constants.CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    ///////// ФУНКЦИЯ ЯВНОГО Intent С ПЕРЕДАЧЕЙ ОБЪЕКТА Track С ПРОВЕРКОЙ ДОСТУПНОСТИ КНОПКИ
    private fun intentAudioPlayer(track: Track) {
        if (clickDebounce()) {
            val displayIntent = Intent(this, AudioPlayerActivity::class.java)
            displayIntent.putExtra(Constants.TRACK, track)
            startActivity(displayIntent)
        }
    }

    ///////// ФУНКЦИЯ ПОКАЗА ИСТОРИИ ЛИСТА
    private fun showHistoryList() {
        // инициализируем лист треков адаптера через функцию
        searchAdapterTrack.listTrack = readTrackHistoryList()
        // инициализируем адаптер Recycler
        rvHistory.adapter = searchAdapterTrack
    }

    ///////// ФУНКЦИЯ ЧТЕНИЯ ЛИСТА ИСТОРИИ ТРЕКОВ
    private fun readTrackHistoryList(): List<Track> {
        // читаем файл с sharedPreferences файл истории треков и инициализируем им searchTrackList
        searchTrackList = searchHistory.read()
        // возвращаем List треков
        return searchTrackList.toList()
    }

    // СОХРАНЯЕМ ДАННЫЕ
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val searchText = inputEditText.text.toString()
        outState.putString(Constants.SEARCH_TEXT, searchText)
    }

    // ПОКАЗЫВАЕМ СОЗРАНЕННЫЕ ДАННЫЕ
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val searchText = savedInstanceState.getString(Constants.SEARCH_TEXT)
        inputEditText.setText(searchText)
    }

    // ФУНКЦИЯ ПОКАЗА КРЕСТИКА ОЧИСТКА СТРОКИ ПОИСКА
    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    // ФУНКЦИЯ ПОИСКА - ЗАПРОС И ОТВЕТ СЕРВЕРА
    private fun search() {
        if (inputEditText.text.isNotEmpty()) {

            rvTrack.visibility = View.GONE
            progressBar.visibility = View.VISIBLE

            iTunesService.search(inputEditText.text.toString())
                .enqueue(object : Callback<TrackResponse> {

                    override fun onResponse(
                        call: Call<TrackResponse>,
                        response: Response<TrackResponse>
                    ) {
                        progressBar.visibility = View.GONE
                        if (response.code() == 200) {
                            trackList.clear()
                            if (response.body()?.results?.isNotEmpty() == true) { // лист results не пустой или можно isSuccessful

                                trackList.addAll(response.body()?.results!!)
                                adapterTrack.notifyDataSetChanged()
                                rvTrack.visibility = View.VISIBLE

                            }
                            if (trackList.isEmpty()) { // лист треков пустой
                                showMessageNotFound(
                                    getString(R.string.error_not_found),
                                    ""
                                ) // ничего не найдено

                            } else { // лист треков не  пустой
                                showMessageNotFound("", "")
                            }
                        } else { // ответ кроме 200
                            showMessageNotConnection(
                                getString(R.string.error_connection),
                                response.code().toString()
                            )
                        }
                    }

                    override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                        showMessageNotConnection(
                            getString(R.string.error_connection),
                            t.message.toString()
                        )
                    }

                })
        }
    }

    // ФУНКЦИЯ ПОКАЗА СООБЩЕНИЯ НИЧЕГО НЕ НАЙДЕНО
    private fun showMessageNotFound(text: String, additionalMessage: String) {
        if (text.isNotEmpty()) {
            rvTrack.visibility = View.GONE
            llErrorNothingWasFound.visibility = View.VISIBLE
            trackList.clear()
            adapterTrack.notifyDataSetChanged()
            if (additionalMessage.isNotEmpty()) {
                Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            llErrorNothingWasFound.visibility = View.GONE
        }
    }

    // ФУНКЦИЯ ПОКАЗА СООБЩЕНИЯ ПРОБЛЕМА СО СВЯЗЬЮ
    private fun showMessageNotConnection(text: String, additionalMessage: String) {
        if (text.isNotEmpty()) {
            rvTrack.visibility = View.GONE
            llEerrorConnection.visibility = View.VISIBLE
            trackList.clear()
            adapterTrack.notifyDataSetChanged()
            if (additionalMessage.isNotEmpty()) {
                Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            llEerrorConnection.visibility = View.GONE
        }
    }


}



