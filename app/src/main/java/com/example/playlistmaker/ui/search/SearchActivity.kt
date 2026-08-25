package com.example.playlistmaker.ui.search

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.APPLICATION_PREFERENCES
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.HistorySearchInteractor
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.audioPlayer.AudioPlayerActivity

class SearchActivity : AppCompatActivity() {

    private var searchString: String = SEARCH_DEF

    private val tracksAdapter = TracksAdapter()
    private val historyTracksAdapter = TracksAdapter()
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { search() }
    private var isClickAllowed = true

    private lateinit var searchEditText : EditText
    private lateinit var errorImage : ImageView
    private lateinit var errorText : TextView
    private lateinit var updateBtn : Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tracksInteractor :TracksInteractor
    private lateinit var historyInteractor: HistorySearchInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = statusBar.top)
            insets
        }

        searchEditText = findViewById(R.id.searchEditText)
        errorImage = findViewById(R.id.error_image)
        errorText = findViewById(R.id.error_text)
        updateBtn = findViewById(R.id.update_btn)
        progressBar = findViewById(R.id.progressBar)
        val clearButton = findViewById<FrameLayout>(R.id.clearIcon)
        val backButton = findViewById<Button>(R.id.back)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val historyLinear = findViewById<LinearLayout>(R.id.historyContainer)
        val historyRecyclerView = findViewById<RecyclerView>(R.id.historyRecyclerView)
        val clearHistoryButton = findViewById<Button>(R.id.clear_btn)

        tracksInteractor = Creator.provideTracksInteractor()
        historyInteractor = Creator.provideHistoryInteractor(this)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = tracksAdapter
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = historyTracksAdapter

        fun updateHistoryVisibility() {
            val showHistory = searchEditText.hasFocus() && searchEditText.text.isEmpty() &&
                    historyInteractor.getHistory().isNotEmpty()

            historyLinear.visibility = if (showHistory) View.VISIBLE else View.GONE

            recyclerView.visibility = if (showHistory) View.GONE else View.VISIBLE

            if (showHistory) {
                historyTracksAdapter.submitList(historyInteractor.getHistory())
            }
        }

        fun adapterAction(track: Track) {
            if (clickDebounce()) {
                historyInteractor.addTrack(track)
                updateHistoryVisibility()
                val intent = Intent(this, AudioPlayerActivity::class.java)
                intent.putExtra(AudioPlayerActivity.TRACK, track)
                startActivity(intent)
            }
        }

        tracksAdapter.setOnItemClickListener { track ->
            adapterAction(track)
        }

        historyTracksAdapter.setOnItemClickListener { track ->
            adapterAction(track)
        }

        clearHistoryButton.setOnClickListener {
            historyInteractor.clearHistory()
            updateHistoryVisibility()
        }

        backButton.setOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            searchEditText.setText("")
            searchEditText.clearFocus()
            tracksAdapter.submitList(emptyList())
            clearError()
            updateHistoryVisibility()
            val imm = searchEditText.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                searchString = s?.toString() ?: SEARCH_DEF
                updateHistoryVisibility()
                searchDebounce()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        searchEditText.addTextChangedListener(simpleTextWatcher)

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
                true
            }
            false
        }

        updateBtn.setOnClickListener {
            search()
        }

        searchEditText.setOnFocusChangeListener { view, hasFocus ->
            updateHistoryVisibility()
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_REQUEST, searchString)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchString = savedInstanceState.getString(SEARCH_REQUEST, searchString)
        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        searchEditText.setText(searchString)
    }

    private fun search() {
        clearError()
        if (searchEditText.text.isNotEmpty()) {
            progressBar.visibility = View.VISIBLE
            tracksInteractor.searchTracks(searchEditText.text.toString(), object : TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>) {
                    runOnUiThread {
                        progressBar.visibility = View.GONE
                        tracksAdapter.submitList(foundTracks)
                        if (foundTracks.isEmpty()) {
                            showError(getString(R.string.empty_search), R.drawable.not_found, false)
                        }
                    }
                }

                override fun onError(error: Throwable) {
                    runOnUiThread {
                        progressBar.visibility = View.GONE
                        showError(getString(R.string.network_error_search), R.drawable.network_error, true)
                    }
                }
            })
        }
    }

    private fun clearError() {
        errorImage.visibility = View.GONE
        errorText.visibility = View.GONE
        updateBtn.visibility = View.GONE
    }

    private fun showError(text: String, resImgId: Int, isNetworkError: Boolean) {
        tracksAdapter.submitList(emptyList())
        errorImage.visibility = View.VISIBLE
        errorText.visibility = View.VISIBLE
        errorImage.setImageResource(resImgId)
        errorText.text = text
        if (isNetworkError) updateBtn.visibility = View.VISIBLE
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true}, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    companion object {
        const val SEARCH_REQUEST = "SEARCH_REQUEST"
        const val SEARCH_DEF = ""
        const val SEARCH_DEBOUNCE_DELAY = 2000L
        const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}