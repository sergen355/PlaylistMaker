package com.practicum.playlistmaker.presentation.ui

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.data.api.ITunesApi
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.model.Track
import com.practicum.playlistmaker.data.impl.TrackResponse
import com.practicum.playlistmaker.domain.api.SearchHistoryInteractor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class SearchActivity : AppCompatActivity() {

    private var stringValue = ""
    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var back: ImageView
    private lateinit var trackRecyclerView: RecyclerView
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderUpdateButton: Button
    private lateinit var placeholderSearch: TextView
    private lateinit var historyClearButton: Button
    private lateinit var searchHistory: SearchHistoryInteractor
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var searchProgressBar: ProgressBar

    val trackList: MutableList<Track> = ArrayList()
    val trackAdapter = TrackAdapter(trackList)
    private val retrofit = Retrofit.Builder().baseUrl("https://itunes.apple.com")
        .addConverterFactory(GsonConverterFactory.create()).build()
    private val iTunesApi = retrofit.create<ITunesApi>()
    private val searchRunnable = Runnable { trackSearch() }
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    companion object {
        const private val SEARCH_STRING = "SEARCH_STRING"
        const private val PLAYLIST_MAKER_HISTORY = "playlist_maker_history"
        const private val CLICK_DEBOUNCE_DELAY = 1000L
        const private val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_HISTORY, MODE_PRIVATE)
        searchHistory = Creator.provideSearchHistoryInteractor(sharedPrefs)

        historyAdapter = TrackAdapter(searchHistory.getTrackList())
        trackRecyclerView = findViewById(R.id.track_recycler_view)
        historyRecyclerView = findViewById(R.id.history_recycler_view)
        placeholderMessage = findViewById(R.id.placeholder_message)
        placeholderImage = findViewById(R.id.placeholder_image)
        placeholderUpdateButton = findViewById(R.id.placeholder_button)
        placeholderSearch = findViewById(R.id.placeholder_history_text)
        historyClearButton = findViewById(R.id.clear_history_button)
        inputEditText = findViewById(R.id.edit_text)
        clearButton = findViewById(R.id.clear_icon)
        back = findViewById(R.id.back)
        searchProgressBar = findViewById(R.id.search_progress_bar)

        trackRecyclerView.adapter = trackAdapter
        historyRecyclerView.adapter = historyAdapter

        searchHistory.getHistoryList()

        showTrackHistory()

        inputEditText.requestFocus()

        back.setOnClickListener {
            this.finish()
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")
            val view: View? = this.currentFocus
            if (view != null) {
                val inputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                trackList.clear()
                trackAdapter.notifyDataSetChanged()
                hidePlaceholders()
            }
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                if (inputEditText.hasFocus() && s.isNullOrEmpty()) {
                    showTrackHistory()
                } else {
                    hideTrackHistory()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                stringValue = inputEditText.text.toString()
                searchDebounce()
            }
        }

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                trackSearch()
            }
            false
        }

        inputEditText.addTextChangedListener(simpleTextWatcher)

        historyClearButton.setOnClickListener {
            searchHistory.clearHistory()
            hideTrackHistory()
        }


        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && inputEditText.text.isEmpty()) {
                showTrackHistory()
            } else {
                hideTrackHistory()
            }
        }

    }

    private fun trackSearch() {
        hidePlaceholders()
        searchProgressBar.visibility = View.VISIBLE
        trackRecyclerView.visibility = View.GONE
        iTunesApi.search("song", inputEditText.text.toString())
            .enqueue(object : Callback<TrackResponse> {
                override fun onResponse(
                    call: Call<TrackResponse>, response: Response<TrackResponse>
                ) {
                    if (response.code() == 200) {
                        trackList.clear()
                        Log.d("To check response", response.toString())
                        Log.d("Response code", response.code().toString())
                        if (response.body()?.results?.isNotEmpty() == true) {
                            trackList.addAll(response.body()?.results!!)
                            trackAdapter.notifyDataSetChanged()
                            searchProgressBar.visibility = View.GONE
                            trackRecyclerView.visibility = View.VISIBLE
                        }
                        if (trackList.isEmpty()) {
                            showMessage(getString(R.string.nothing_found), "")
                            if (isDarkTheme()) {
                                showImage(R.drawable.placeholder_songs_nothing_found_dark)
                            } else {
                                showImage(R.drawable.placeholder_songs_nothing_found_light)
                            }
                        } else {
                            showMessage("", "")
                        }
                    } else {
                        showMessage(
                            getString(R.string.something_went_wrong), response.code().toString()
                        )
                        if (isDarkTheme()) {
                            showImage(R.drawable.placeholder_songs_no_connection_dark)
                        } else {
                            showImage(R.drawable.placeholder_songs_no_connection_light)
                        }
                        showUpdateButton()
                    }
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    showMessage(
                        getString(R.string.something_went_wrong), t.toString()
                    )
                    if (isDarkTheme()) {
                        showImage(R.drawable.placeholder_songs_no_connection_dark)
                    } else {
                        showImage(R.drawable.placeholder_songs_no_connection_light)
                    }
                    showUpdateButton()
                }
            })
    }

    private fun showMessage(text: String, additionalMessage: String) {
        if (text.isNotEmpty()) {
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            placeholderMessage.text = text
            placeholderMessage.visibility = View.VISIBLE
            searchProgressBar.visibility = View.GONE
            if (additionalMessage.isNotEmpty()) {
                Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showImage(image: Int) {
        placeholderImage.setImageResource(image)
        placeholderImage.visibility = View.VISIBLE
    }

    private fun showUpdateButton() {
        placeholderUpdateButton.visibility = View.VISIBLE
        placeholderUpdateButton.setOnClickListener {
            trackSearch()
        }
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun hidePlaceholders() {
        placeholderMessage.visibility = View.GONE
        placeholderImage.visibility = View.GONE
        placeholderUpdateButton.visibility = View.GONE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_STRING, stringValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        stringValue = savedInstanceState.getString(SEARCH_STRING, "")
        inputEditText.setText(stringValue)
    }

    private fun showTrackHistory() {
        trackList.clear()
        searchHistory.getHistoryList()
        historyAdapter.notifyDataSetChanged()
        if (historyAdapter.itemCount > 0) {
            historyClearButton.visibility = View.VISIBLE
            placeholderSearch.visibility = View.VISIBLE
            historyRecyclerView.visibility = View.VISIBLE
        }
        trackRecyclerView.visibility = View.GONE
    }

    private fun hideTrackHistory() {
        searchHistory.clearTrackList()
        historyAdapter.notifyDataSetChanged()
        historyClearButton.visibility = View.GONE
        placeholderSearch.visibility = View.GONE
        historyRecyclerView.visibility = View.GONE
        trackRecyclerView.visibility = View.VISIBLE
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    fun onTrackClick(view: View?) {
        if (clickDebounce()) {
            val trackId = view?.findViewById<TextView>(R.id.track_id)?.text
            var track: Track?
            if (!trackId.isNullOrEmpty()) {
                track = trackList.find { it.trackId == trackId.toString().toInt() }
                if (track != null) {
                    searchHistory.addTrack(track)
                } else {
                    track = searchHistory.getTrackList().find {
                        it.trackId == trackId.toString().toInt()
                    }
                }

                val displayIntent = Intent(this, PlayerActivity::class.java)
                displayIntent.putExtra("track", track)
                startActivity(displayIntent)

            }
        }
    }

    fun Context.isDarkTheme(): Boolean {
        return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }
}

