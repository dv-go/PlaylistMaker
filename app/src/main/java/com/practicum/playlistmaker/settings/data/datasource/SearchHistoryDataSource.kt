package com.practicum.playlistmaker.settings.data.datasource

import android.content.SharedPreferences

class SearchHistoryDataSource(private val sharedPreferences: SharedPreferences) {

    fun getHistoryJson(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    fun saveHistoryJson(key: String, json: String) {
        sharedPreferences.edit().putString(key, json).apply()
    }

    fun clearHistory(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }
}
