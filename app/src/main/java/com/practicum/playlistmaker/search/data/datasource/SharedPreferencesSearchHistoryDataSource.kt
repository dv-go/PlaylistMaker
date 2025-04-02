package com.practicum.playlistmaker.search.data.datasource

import android.content.SharedPreferences

class SharedPreferencesSearchHistoryDataSource(
    private val sharedPreferences: SharedPreferences
) : SearchHistoryDataSource {

    override fun getHistoryJson(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    override fun saveHistoryJson(key: String, json: String) {
        sharedPreferences.edit().putString(key, json).apply()
    }

    override fun clearHistory(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }
}
