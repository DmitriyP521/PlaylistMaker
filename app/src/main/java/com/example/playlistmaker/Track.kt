package com.example.playlistmaker

import java.text.SimpleDateFormat
import java.util.Locale

data class Track(
    val trackName: String?,
    val artistName: String?,
    val trackTimeMillis: Int?,
    val artworkUrl100: String?
) {
    val formattedTrackName: String
        get() = if (trackName.isNullOrEmpty()) "Unknown"
                else if (trackName.length > 21) trackName.take(20) + "..."
                else trackName

    val formattedArtistName: String
        get() = if (artistName.isNullOrEmpty()) "Unknown"
                else if (artistName.length > 26) artistName.take(25) + "..."
                else artistName

    val formattedTrackTime: String
        get() = if (trackTimeMillis == null) "Unknown"
                else SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)

    val formattedArtworkUrl: String
        get() = artworkUrl100 ?: ""

}