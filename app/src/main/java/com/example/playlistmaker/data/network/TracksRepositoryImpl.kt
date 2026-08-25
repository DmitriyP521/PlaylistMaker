package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.TracksRequest
import com.example.playlistmaker.data.dto.TracksResponse
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Track

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(expression: String): Result<List<Track>> {
        return try {
            val response = networkClient.doRequest(TracksRequest(expression))
            when (response.resultCode) {
                200 -> {
                    val tracks = (response as TracksResponse).results.map {
                        Track(
                            it.trackId, it.trackName, it.artistName, it.trackTimeMillis,
                            it.artworkUrl100, it.collectionName, it.releaseDate,
                            it.primaryGenreName, it.country, it.previewUrl
                        )
                    }
                    Result.success(tracks)
                }
                else -> Result.failure(Exception())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}