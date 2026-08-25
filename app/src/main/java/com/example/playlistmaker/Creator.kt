package com.example.playlistmaker


import android.content.Context
import com.example.playlistmaker.data.SettingsRepositoryImpl
import com.example.playlistmaker.data.network.HistorySearchRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.network.TracksRepositoryImpl
import com.example.playlistmaker.domain.api.HistorySearchInteractor
import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.api.SettingsRepository
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.HistorySearchInteractorImpl
import com.example.playlistmaker.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.domain.impl.TracksInteractorImpl

object Creator {
    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    fun provideSettingsInteractor(context: Context) : SettingsInteractor {
        val prefs = context.getSharedPreferences("playlist_preferences", Context.MODE_PRIVATE)
        val repository: SettingsRepository = SettingsRepositoryImpl(prefs)
        return SettingsInteractorImpl(repository)
    }

    fun provideHistoryInteractor(context: Context): HistorySearchInteractor {
        val prefs = context.getSharedPreferences("playlist_preferences", Context.MODE_PRIVATE)
        val repository = HistorySearchRepositoryImpl(prefs)
        return HistorySearchInteractorImpl(repository)
    }
}