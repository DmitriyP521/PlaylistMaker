package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val sharedPreferences: SharedPreferences) {

    private val gson = Gson()
    private val HISTORY_KEY = "HISTORY_KEY"

    fun getHistory(): List<Track> {
        val json = sharedPreferences.getString(HISTORY_KEY, null) ?: return emptyList()
        val tracksArray = gson.fromJson(json, Array<Track>::class.java)
        return tracksArray?.toList() ?: emptyList()
    }

    fun addTrack(track: Track) {
        val currentHistory = getHistory().toMutableList()

        currentHistory.removeAll { it.trackId == track.trackId }
        currentHistory.add(0, track)
        while (currentHistory.size > 10) {
            currentHistory.removeAt(currentHistory.lastIndex)
        }
        saveHistory(currentHistory)
    }

    fun clearHistory() {
        saveHistory(emptyList())
    }

    private fun saveHistory(tracks: List<Track>) {
        val json = gson.toJson(tracks)
        sharedPreferences.edit()
            .putString(HISTORY_KEY, json)
            .apply()
    }

    fun isEmpty(): Boolean {
        return getHistory().isEmpty()
    }
}