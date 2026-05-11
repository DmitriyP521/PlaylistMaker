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

class SearchActivity : AppCompatActivity() {

    private var searchString: String = SEARCH_DEF
    private val ITunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(ITunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val ItunesApiService = retrofit.create(ITunesApiService::class.java)
    private val tracks = ArrayList<Track>()
    private val tracksAdapter = TracksAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = statusBar.top)
            insets
        }

        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        val clearButton = findViewById<FrameLayout>(R.id.clearIcon)
        val backButton = findViewById<Button>(R.id.back)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val errorImage = findViewById<ImageView>(R.id.error_image)
        val errorText = findViewById<TextView>(R.id.error_text)
        val updateBtn = findViewById<Button>(R.id.update_btn)

        fun clearError() {
            errorImage.visibility = View.GONE
            errorText.visibility = View.GONE
            updateBtn.visibility = View.GONE
        }

        fun showError(text: String, resImgId: Int, isNetworkError: Boolean) {
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
                        if (response.code() == 200) {
                            tracks.clear()
                            if (response.body()?.results?.isNotEmpty() == true) {
                                tracks.addAll(response.body()?.results!!)
                                tracksAdapter.notifyDataSetChanged()
                            }
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

        tracksAdapter.tracks = tracks
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = tracksAdapter

        backButton.setOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            searchEditText.setText("")
            searchEditText.clearFocus()
            tracks.clear()
            tracksAdapter.notifyDataSetChanged()
            clearError()
            val imm = searchEditText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                searchString = s?.toString() ?: SEARCH_DEF
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