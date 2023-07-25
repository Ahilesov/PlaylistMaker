package searchtrack

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private lateinit var inputEditText: EditText
    private lateinit var backButton: ImageView
    private lateinit var clearButton: ImageView
    private lateinit var rvTrack: RecyclerView
    private lateinit var errorNothingWasFound: LinearLayout
    private lateinit var errorConnection: LinearLayout
    private lateinit var refreshButton: Button
    private lateinit var adapterTrack: TrackAdapter

    companion object {
        private const val SEARCH_TEXT = "SEARCH_TEXT"
    }

    private val iTunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(TrackApi::class.java)
    private val trackList = ArrayList<Track>()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        adapterTrack = TrackAdapter(trackList)

        backButton = findViewById<ImageView>(R.id.back)
        inputEditText = findViewById<EditText>(R.id.inputEditText)
        clearButton = findViewById<ImageView>(R.id.clearIcon)
        rvTrack = findViewById<RecyclerView>(R.id.rvTrack)
        errorNothingWasFound = findViewById<LinearLayout>(R.id.errorNotFound)
        errorConnection = findViewById<LinearLayout>(R.id.errorConnection)
        refreshButton = findViewById<Button>(R.id.button_refresh)



        backButton.setOnClickListener{// кнопка назад
            finish()
        }

        clearButton.setOnClickListener {// кнопка крестик в поиске
            inputEditText.setText("")
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager // прячем клавиатуру с нажатием на крестик
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)

            trackList.clear()
            adapterTrack.notifyDataSetChanged()
            errorNothingWasFound.visibility = View.GONE
            errorConnection.visibility = View.GONE

        }

        refreshButton.setOnClickListener { // кнопка обновить

            search()
        }

        val simpleTextWatcher = object : TextWatcher { // ввод текста в строку поиска
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // реализация позже
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
                // реализация позже
            }
        }

        inputEditText.addTextChangedListener(simpleTextWatcher)
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                Log.e("My log", "Кнопка отработала")
                search()
                true
            }
            false
        }

        rvTrack.adapter = adapterTrack
    }

    override fun onSaveInstanceState(outState: Bundle) { // сохраняем данные
        super.onSaveInstanceState(outState)
        val searchText = inputEditText.text.toString()
        outState.putString(SEARCH_TEXT, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) { // показываем сохранненые данные
        super.onRestoreInstanceState(savedInstanceState)
        val searchText = savedInstanceState.getString(SEARCH_TEXT)
        inputEditText.setText(searchText)
    }

    private fun clearButtonVisibility(s: CharSequence?): Int { //функция показа крестика очистка строки поиска
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun search() {

        if (inputEditText.text.isNotEmpty()) {
            iTunesService.search(inputEditText.text.toString())
                .enqueue(object : Callback<TrackResponse> {

                    override fun onResponse(
                        call: Call<TrackResponse>,
                        response: Response<TrackResponse>
                    ) {
                        if (response.code() == 200) {

                            trackList.clear()
                            if (response.body()?.results?.isNotEmpty() == true) { // лист results не пустой или можно isSuccessful
                                trackList.addAll(response.body()?.results!!)
                                adapterTrack.notifyDataSetChanged()
                                rvTrack.visibility = View.VISIBLE

                            }
                            if (trackList.isEmpty()) { // лист треков пустой
                                showMessageNotFound(getString(R.string.error_not_found), "") // ничего не найдено

                            } else { // лист треков не  пустой
                                showMessageNotFound("", "")
                            }
                        } else { // ответ кроме 200
                            showMessageNotConnection(getString(R.string.error_connection), response.code().toString())
                        }
                    }

                    override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                        showMessageNotConnection(getString(R.string.error_connection), t.message.toString())
                    }

                })
        }
    }

    private fun showMessageNotFound(text: String, additionalMessage: String) {
        if (text.isNotEmpty()) {
            rvTrack.visibility = View.GONE
            errorNothingWasFound.visibility = View.VISIBLE
            trackList.clear()
            adapterTrack.notifyDataSetChanged()
            if (additionalMessage.isNotEmpty()) {
                Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            errorNothingWasFound.visibility = View.GONE
        }
    }

    private fun showMessageNotConnection(text: String, additionalMessage: String) {
        if (text.isNotEmpty()) {
            rvTrack.visibility = View.GONE
            errorConnection.visibility = View.VISIBLE
            trackList.clear()
            adapterTrack.notifyDataSetChanged()
            if (additionalMessage.isNotEmpty()) {
                Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            errorConnection.visibility = View.GONE
        }
    }


}



