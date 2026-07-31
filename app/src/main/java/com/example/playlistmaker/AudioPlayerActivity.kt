package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class AudioPlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_audio_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = statusBar.top)
            insets
        }

        val backButton = findViewById<Button>(R.id.back)
        val artworkImageView = findViewById<ImageView>(R.id.artworkImageView)
        val trackNameTextView = findViewById<TextView>(R.id.trackNameTextView)
        val artistNameTextView = findViewById<TextView>(R.id.artistNameTextView)
        val trackDurationTextView = findViewById<TextView>(R.id.trackDurationTextView)
        val durationValueTextView = findViewById<TextView>(R.id.durationValueTextView)
        val albumLabelTextView = findViewById<TextView>(R.id.albumLabelTextView)
        val albumValueTextView = findViewById<TextView>(R.id.albumValueTextView)
        val yearLabelTextView = findViewById<TextView>(R.id.yearLabelTextView)
        val yearValueTextView = findViewById<TextView>(R.id.yearValueTextView)
        val genreLabelTextView = findViewById<TextView>(R.id.genreLabelTextView)
        val genreValueTextView = findViewById<TextView>(R.id.genreValueTextView)
        val countryLabelTextView = findViewById<TextView>(R.id.countryLabelTextView)
        val countryValueTextView = findViewById<TextView>(R.id.countryValueTextView)

        var track = intent.getParcelableExtra(TRACK, Track::class.java)!!

        Glide
            .with(this)
            .load(track.getCoverArtwork())
            .fitCenter()
            .transform(RoundedCorners(dpToPx(8f, this)))
            .placeholder(R.drawable.player_stub)
            .error(R.drawable.player_stub)
            .into(artworkImageView)
        trackNameTextView.text = track.trackName
        artistNameTextView.text = track.artistName
        trackDurationTextView.text = track.formattedTrackTime
        durationValueTextView.text = track.formattedTrackTime
        albumValueTextView.text = track.collectionName
        yearValueTextView.text = track.formattedYear()
        genreValueTextView.text = track.primaryGenreName
        countryValueTextView.text = track.country

        if (track.collectionName == null) {
            albumLabelTextView.visibility = View.GONE
            albumValueTextView.visibility = View.GONE
        }

        if (track.formattedYear() == null) {
            yearLabelTextView.visibility = View.GONE
            yearValueTextView.visibility = View.GONE
        }

        if (track.primaryGenreName == null) {
            genreLabelTextView.visibility = View.GONE
            genreValueTextView.visibility = View.GONE
        }

        if (track.country == null) {
            countryLabelTextView.visibility = View.GONE
            countryValueTextView.visibility = View.GONE
        }

        backButton.setOnClickListener {
            finish()
        }
    }
    fun dpToPx(dp: Float, context: Context) : Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }

    companion object {
        const val TRACK = "track"
    }
}