package com.practicum.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val sharedPreferences: SharedPreferences) {

    private val HISTORY_KEY = "search_history"
    private val MAX_HISTORY_SIZE = 10
    private val gson = Gson()

    fun getHistory(): List<Track> {
        val historyJson = sharedPreferences.getString(HISTORY_KEY, null) ?: return emptyList()
        val type = object : TypeToken<List<Track>>() {}.type
        return gson.fromJson(historyJson, type)
    }

    fun saveToHistory(track: Track) {
        val currentHistory = getHistory().toMutableList()
        currentHistory.removeAll { it.trackId == track.trackId }
        currentHistory.add(0, track)
        if (currentHistory.size > MAX_HISTORY_SIZE) {
            currentHistory.removeAt(currentHistory.lastIndex)
        }
        saveHistory(currentHistory)
    }

    fun clearHistory() {
        sharedPreferences.edit().remove(HISTORY_KEY).apply()
    }

    private fun saveHistory(history: List<Track>) {
        val historyJson = gson.toJson(history)
        sharedPreferences.edit().putString(HISTORY_KEY, historyJson).apply()
    }
}
