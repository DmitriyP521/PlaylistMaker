package com.example.playlistmaker.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Locale

@Parcelize
data class Track(
    val trackId: Int?,
    val trackName: String?,
    val artistName: String?,
    val trackTimeMillis: Int?,
    val artworkUrl100: String?,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String
) : Parcelable
{
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

    fun formattedYear() = releaseDate?.take(4)

    fun getCoverArtwork() = artworkUrl100?.replaceAfterLast('/',"512x512bb.jpg")
}