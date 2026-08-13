package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.HistorySearchInteractor
import com.example.playlistmaker.domain.api.HistorySearchRepository
import com.example.playlistmaker.domain.models.Track

class HistorySearchInteractorImpl (
    private val repository : HistorySearchRepository
) : HistorySearchInteractor {
    override fun addTrack(track: Track) {
        val currentHistory = repository.getHistory().toMutableList()

        currentHistory.removeAll { it.trackId == track.trackId }
        currentHistory.add(0, track)
        while (currentHistory.size > MAX_HISTORY_SIZE) {
            currentHistory.removeAt(currentHistory.lastIndex)
        }
        repository.saveHistory(currentHistory)
    }
    override fun getHistory(): List<Track> {
        return repository.getHistory()
    }

    override fun clearHistory() {
        repository.clearHistory()
    }

    companion object {
        private const val MAX_HISTORY_SIZE = 10
    }
}