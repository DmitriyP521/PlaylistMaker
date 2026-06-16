package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
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
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.Call
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory

const val ITUNES_BASE_URL = "https://itunes.apple.com/"
class SearchActivity : AppCompatActivity() {

    private var searchString: String = SEARCH_DEF
    private val retrofit = Retrofit.Builder()
        .baseUrl(ITUNES_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val ItunesApiService = retrofit.create(ITunesApiService::class.java)
    private val tracksAdapter = TracksAdapter()
    private val historyTracksAdapter = TracksAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = statusBar.top)
            insets
        }

        val sharedPrefs = getSharedPreferences(APPLICATION_PREFERENCES, MODE_PRIVATE)
        val searchHistory = SearchHistory(sharedPrefs)

        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        val clearButton = findViewById<FrameLayout>(R.id.clearIcon)
        val backButton = findViewById<Button>(R.id.back)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val errorImage = findViewById<ImageView>(R.id.error_image)
        val errorText = findViewById<TextView>(R.id.error_text)
        val updateBtn = findViewById<Button>(R.id.update_btn)
        val historyLinear = findViewById<LinearLayout>(R.id.historyContainer)
        val historyRecyclerView = findViewById<RecyclerView>(R.id.historyRecyclerView)
        val clearHistoryButton = findViewById<Button>(R.id.clear_btn)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = tracksAdapter
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = historyTracksAdapter

        fun updateHistoryVisibility() {
            val showHistory = searchEditText.hasFocus() && searchEditText.text.isEmpty() && !searchHistory.isEmpty()

            historyLinear.visibility = if (showHistory) View.VISIBLE else View.GONE

            recyclerView.visibility = if (showHistory) View.GONE else View.VISIBLE

            if (showHistory) {
                historyTracksAdapter.submitList(searchHistory.getHistory())
            }
        }

        fun clearError() {
            errorImage.visibility = View.GONE
            errorText.visibility = View.GONE
            updateBtn.visibility = View.GONE
        }

        fun showError(text: String, resImgId: Int, isNetworkError: Boolean) {
            tracksAdapter.submitList(emptyList())
            errorImage.visibility = View.VISIBLE
            errorText.visibility = View.VISIBLE
            errorImage.setImageResource(resImgId)
            errorText.text = text
            if (isNetworkError) updateBtn.visibility = View.VISIBLE
        }

        fun search() {
            clearError()
            if (searchEditText.text.isNotEmpty()) {
                ItunesApiService.search(searchEditText.text.toString()).enqueue(object : Callback<TracksResponse> {
                    override fun onResponse(call: Call<TracksResponse>, response: Response<TracksResponse>) {
                        if (response.isSuccessful) {
                            val tracks = response.body()?.results ?: emptyList()
                            tracksAdapter.submitList(tracks)
                            if (tracks.isEmpty()) {
                                showError(getString(R.string.empty_search), R.drawable.not_found, false)
                            }
                        }
                    }
                    override fun onFailure(call: Call<TracksResponse?>, t: Throwable) {
                        showError(getString(R.string.network_error_search), R.drawable.network_error, true)
                    }
                })
            }
        }


        tracksAdapter.setOnItemClickListener { track ->
            searchHistory.addTrack(track)
            updateHistoryVisibility()
        }

        historyTracksAdapter.setOnItemClickListener { track ->
            searchHistory.addTrack(track)
            updateHistoryVisibility()
        }

        clearHistoryButton.setOnClickListener {
            searchHistory.clearHistory()
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
            val imm = searchEditText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                searchString = s?.toString() ?: SEARCH_DEF
                updateHistoryVisibility()
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

    companion object {
        const val SEARCH_REQUEST = "SEARCH_REQUEST"
        const val SEARCH_DEF = ""
    }
}