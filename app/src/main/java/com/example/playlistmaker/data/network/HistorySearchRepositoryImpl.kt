package com.example.playlistmaker.data.network

import android.content.SharedPreferences
import com.example.playlistmaker.domain.api.HistorySearchRepository
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson

class HistorySearchRepositoryImpl(
    private val prefs: SharedPreferences
) : HistorySearchRepository {
    private val gson = Gson()

    override fun clearHistory() {
        saveHistory(emptyList())
    }

    override fun getHistory(): List<Track> {
        val json = prefs.getString(HISTORY_KEY, null) ?: return emptyList()
        val tracksArray = gson.fromJson(json, Array<Track>::class.java)
        return tracksArray?.toList() ?: emptyList()
    }

    override fun saveHistory(tracks: List<Track>) {
        val json = gson.toJson(tracks)
        prefs.edit()
            .putString(HISTORY_KEY, json)
            .apply()
    }

    companion object {
        private const val HISTORY_KEY = "history_key"
    }
}