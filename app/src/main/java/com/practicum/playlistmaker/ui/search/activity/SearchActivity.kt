package com.practicum.playlistmaker.ui.search.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher

import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R

import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.domain.model.SearchStatuses
import com.practicum.playlistmaker.domain.model.Track
import com.practicum.playlistmaker.domain.search.SearchHistoryInteractor
import com.practicum.playlistmaker.ui.player.activity.PlayerActivity
import com.practicum.playlistmaker.ui.search.view_model.SearchViewModel


const val PLAYLIST_HISTORY = "playlist_history"

class SearchActivity : AppCompatActivity() {

    private lateinit var searchViewModel: SearchViewModel
    private val CLICK_DEBOUNCE_DELAY = 1000L
    private val SEARCH_DEBOUNCE_DELAY = 2000L
    private val searchRunnable = Runnable { searchTrack() }
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var searchHistory: SearchHistoryInteractor
    private var searchText: String = ""
    private lateinit var adapter: TrackAdapter
    private lateinit var adapterHistory: TrackAdapter

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
    private lateinit var searchProgressBar: ProgressBar


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


    private fun setElements(status: SearchStatuses) {
        if (status == SearchStatuses.SUCCESS) {
            searchProgressBar.visibility = View.GONE
            placeholderImage.visibility = View.GONE
            placeholderMessage.visibility = View.GONE
            placeholderUpdateButton.visibility = View.GONE
            trackRecyclerView.visibility = View.VISIBLE
        } else if (status == SearchStatuses.CONNECTION_ERROR) {
            searchProgressBar.visibility = View.GONE
            placeholderImage.setImageResource(R.drawable.placeholder_songs_no_connection)
            placeholderMessage.setText(R.string.something_went_wrong)
            placeholderImage.visibility = View.VISIBLE
            placeholderMessage.visibility = View.VISIBLE
            placeholderUpdateButton.visibility = View.VISIBLE
            trackRecyclerView.visibility = View.GONE
        } else if (status == SearchStatuses.EMPTY_RESULT) {
            searchProgressBar.visibility = View.GONE
            placeholderImage.setImageResource(R.drawable.placeholder_songs_nothing_found)
            placeholderMessage.setText(R.string.nothing_found)
            placeholderImage.visibility = View.VISIBLE
            placeholderMessage.visibility = View.VISIBLE
            placeholderUpdateButton.visibility = View.GONE
            trackRecyclerView.visibility = View.GONE
        } else if (status == SearchStatuses.IN_PROGRESS) {
            searchProgressBar.visibility = View.VISIBLE
            placeholderImage.visibility = View.GONE
            placeholderMessage.visibility = View.GONE
            placeholderUpdateButton.visibility = View.GONE
            trackRecyclerView.visibility = View.GONE
        }
    }


    private fun searchTrack() {

        if (searchText.isNotEmpty()) {
            searchViewModel.searchTrack(searchText)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPrefs = getSharedPreferences(PLAYLIST_HISTORY, MODE_PRIVATE)

        searchHistory = Creator.provideSearchHistoryInteractor(sharedPrefs)

        searchViewModel = ViewModelProvider(
            this,
            SearchViewModel.getViewModelFactory(sharedPrefs)
        )[SearchViewModel::class.java]


        setContentView(R.layout.activity_search)

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



        back.setOnClickListener {
            this.finish()
        }

        clearButton.setOnClickListener {
            searchViewModel.clearSearchText()
            inputEditText.setText("")
        }

        searchViewModel.searchStatus.observe(this) {
            setElements(it)
        }

        searchViewModel.getTrackList().observe(this) { trackList ->
            adapter = TrackAdapter({
                onTrackClick(it)
            }, trackList)

            trackRecyclerView.layoutManager = LinearLayoutManager(this)
            trackRecyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        }



        searchViewModel.getTrackHistoryList().observe(this) { trackList ->
            adapterHistory = TrackAdapter({
                onTrackClick(it)
            }, trackList)

            historyRecyclerView.layoutManager = LinearLayoutManager(this)
            historyRecyclerView.adapter = adapterHistory
            adapterHistory.notifyDataSetChanged()
        }

        searchViewModel.isShowHistoryList.observe(this) {
            if (it) {
                showTrackHistory()
            } else {
                hideTrackHistory()
            }
        }

        searchViewModel.fillHistoryList()

        searchViewModel.selectedTrack.observe(this) { track ->
            track?.let {
                navigateToPlayerActivity(it)
                searchViewModel.onTrackNavigationDone()
            }
        }


        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                searchText = s.toString()
                if (inputEditText.hasFocus() && s.isNullOrEmpty()) {
                    searchViewModel.showHistoryList()
                } else {
                    searchViewModel.hideHistoryList()
                }

                searchDebounce()
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }

        inputEditText.addTextChangedListener(simpleTextWatcher)

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTrack()
            }
            false
        }

        placeholderUpdateButton.setOnClickListener {
            searchTrack()
        }

        historyClearButton.setOnClickListener {
            searchViewModel.clearHistory()
            searchViewModel.hideHistoryList()
        }


        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && inputEditText.text.isEmpty()) {
                searchViewModel.showHistoryList()
            } else {
                searchViewModel.hideHistoryList()
            }
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(SEARCH_TEXT, "")
        inputEditText.setText(searchText)
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun navigateToPlayerActivity(track: Track) {
        val displayIntent = Intent(this, PlayerActivity::class.java)
        displayIntent.putExtra("track", track)
        startActivity(displayIntent)
    }

    private fun showTrackHistory() {
        if (adapterHistory.itemCount > 0) {
            historyClearButton.visibility = View.VISIBLE
            placeholderSearch.visibility = View.VISIBLE
            historyRecyclerView.visibility = View.VISIBLE
        }
        trackRecyclerView.visibility = View.GONE
    }

    private fun hideTrackHistory() {
        historyClearButton.visibility = View.GONE
        placeholderSearch.visibility = View.GONE
        historyRecyclerView.visibility = View.GONE
        trackRecyclerView.visibility = View.VISIBLE
    }

    fun onTrackClick(trackId: Int) {
        if (clickDebounce()) {
            searchViewModel.clickTrack(trackId)
        }
    }

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
    }


}