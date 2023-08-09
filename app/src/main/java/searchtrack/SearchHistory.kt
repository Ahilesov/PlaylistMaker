package searchtrack

import android.content.SharedPreferences
import com.google.gson.Gson
import kotlin.system.exitProcess



// при чтении  sharedPreferences работаем с массивом, при записи с ArrayLis
//
// t

class SearchHistory(private val sharedPreferences: SharedPreferences) {

//    private val historyTrackList = read()
//    private var trackCount = historyTrackList.size
    private var searchHistoryTrackList: MutableList<Track> = mutableListOf()


    // чтение SharedPreferences
    fun read(): Array<Track> {
        // читаем данные по ключу, если данных нет - ворачиваем null, делаем Элвис если null, то
        // вернуть пустой массив
        return Gson().fromJson(
            sharedPreferences.getString(HISTORY_TRACK_KEY, null) ?: return emptyArray<Track>(),
            Array<Track>::class.java
        )

    }

    // запись в SharedPreferences
    private fun write(track: Track) {
        // чтобы записать нужно получить возможно сущ-ий лист
        searchHistoryTrackList = read().toMutableList()
//        проверить условия по добавлению дубли по ID
//        если есть ID в списке, удаляю существующий и новый ставлю в начало списка
//        если треков больше 10, удалить последний трек
//        после добвления трека, сначала добавляю трек потом считаю сколько треков в листе, и если их болше 10 удалялю последний
        if (searchHistoryTrackList.contains(track)) {
            searchHistoryTrackList.remove(track)
        }
        searchHistoryTrackList.add(0, track)

        if (searchHistoryTrackList.size > 10) {
            searchHistoryTrackList.removeLast()
        }

        sharedPreferences.edit()
            .putString(HISTORY_TRACK_KEY, Gson().toJson(searchHistoryTrackList))
            .apply()
    }

    private fun clear() { // очищаем файл
        searchHistoryTrackList.clear()
        sharedPreferences.edit()
            .clear()
            .apply()
    }

    private fun createJsonFromTrackList(tracks: List<Track>): String {
        return Gson().toJson(tracks)
    }

    private fun createTracksFromJson(json: String): Array<Track> {
        return Gson().fromJson(json, Array<Track>::class.java)
    }

}
