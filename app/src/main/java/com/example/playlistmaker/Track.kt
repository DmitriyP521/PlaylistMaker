package com.example.playlistmaker

data class Track(
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl100: String
) {
    val formattedTrackName: String
        get() = if (trackName.length > 21) trackName.take(20) + "..." else trackName

    val formattedArtistName: String
        get() = if (artistName.length > 26) artistName.take(25) + "..." else artistName
}