package searchtrack

import android.content.SharedPreferences
import com.google.gson.Gson
import kotlin.system.exitProcess


class SearchHistory(private val sharedPreferences: SharedPreferences) {

    private var searchHistoryTrackList: MutableList<Track> = mutableListOf()

    // чтение SharedPreferences
    fun read(): Array<Track> {
        // читаем данные по ключу, если данных нет - ворачиваем null, делаем Элвис если null, то
        // вернуть пустой массив
//        return Gson().fromJson(
//            sharedPreferences.getString(HISTORY_TRACK_KEY, null) ?: return emptyArray(),
//            Array<Track>::class.java
//        )
        return sharedPreferences.getString(HISTORY_TRACK_KEY, null)?.let {
            Gson().fromJson(it, Array<Track>::class.java)
        } ?: emptyArray()
    }

    // запись в SharedPreferences
    fun write(track: Track) {
        searchHistoryTrackList = read().toMutableList()
        /*если есть ID в списке, удаляю существующий и новый ставлю в начало списка
        если треков больше 10, удалить последний трек
        после добвления трека, сначала добавляю трек потом считаю сколько треков в листе, и если их
        больше 10 удалялю последний */
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

    fun clear() { // очищаем файл
        sharedPreferences.edit()
            .clear()
            .apply()
    }

}
