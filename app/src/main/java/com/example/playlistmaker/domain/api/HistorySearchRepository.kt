package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface HistorySearchRepository {
    fun getHistory(): List<Track>
    fun clearHistory()
    fun saveHistory(tracks: List<Track>)
}