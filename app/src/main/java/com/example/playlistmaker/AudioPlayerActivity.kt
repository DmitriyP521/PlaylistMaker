package com.example.playlistmaker

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

private var mediaPlayer = MediaPlayer()
private lateinit var playButton : ImageButton
private lateinit var trackDurationTextView : TextView
private val handler = Handler(Looper.getMainLooper())

private var updateTimeRunnable = object : Runnable {
    override fun run() {
        mediaPlayer.let { player ->
            if (player.isPlaying) {
                val currentPosition = player.currentPosition
                val seconds = currentPosition / 1000 % 60
                trackDurationTextView.text = String.format("00:%02d", seconds)
            }
        }
        handler.postDelayed(this, 500)
    }
}
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
        val durationValueTextView = findViewById<TextView>(R.id.durationValueTextView)
        val albumLabelTextView = findViewById<TextView>(R.id.albumLabelTextView)
        val albumValueTextView = findViewById<TextView>(R.id.albumValueTextView)
        val yearLabelTextView = findViewById<TextView>(R.id.yearLabelTextView)
        val yearValueTextView = findViewById<TextView>(R.id.yearValueTextView)
        val genreLabelTextView = findViewById<TextView>(R.id.genreLabelTextView)
        val genreValueTextView = findViewById<TextView>(R.id.genreValueTextView)
        val countryLabelTextView = findViewById<TextView>(R.id.countryLabelTextView)
        val countryValueTextView = findViewById<TextView>(R.id.countryValueTextView)

        trackDurationTextView = findViewById(R.id.trackDurationTextView)
        playButton = findViewById(R.id.playButton)

        var track = intent.getParcelableExtra(TRACK, Track::class.java)!!

        preparePlayer(track.previewUrl)
        playButton.setOnClickListener {
            playBackControl()
        }

        mediaPlayer.setOnCompletionListener {
            pausePlayer()
            stopUpdatingTime()
            trackDurationTextView.text = "00:00"
        }

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
        trackDurationTextView.text = "00:00"
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
    private fun preparePlayer(url: String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playButton.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
        }
    }
    private fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        playButton.setImageResource(R.drawable.pause_button)
        startUpdatingTime()
    }
    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        playButton.setImageResource(R.drawable.play_button)
        stopUpdatingTime()
    }

    private fun playBackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun startUpdatingTime() {
        handler.removeCallbacks(updateTimeRunnable)
        handler.post(updateTimeRunnable)
    }

    private fun stopUpdatingTime() {
        handler.removeCallbacks(updateTimeRunnable)
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
        playButton.setImageResource(R.drawable.play_button)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopUpdatingTime()
        mediaPlayer.release()
    }

    fun dpToPx(dp: Float, context: Context) : Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }

    companion object {
        const val TRACK = "track"
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }
    private var playerState = STATE_DEFAULT
}