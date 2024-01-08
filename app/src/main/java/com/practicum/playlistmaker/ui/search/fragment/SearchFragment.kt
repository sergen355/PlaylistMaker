package com.practicum.playlistmaker.ui.search.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater

import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R

import com.practicum.playlistmaker.domain.model.SearchStatus
import com.practicum.playlistmaker.domain.model.Track
import com.practicum.playlistmaker.ui.player.activity.PlayerActivity
import com.practicum.playlistmaker.ui.search.activity.TrackAdapter
import com.practicum.playlistmaker.ui.search.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

import com.practicum.playlistmaker.databinding.FragmentSearchBinding



class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val searchViewModel by viewModel<SearchViewModel>()
    private val CLICK_DEBOUNCE_DELAY = 1000L
    private val SEARCH_DEBOUNCE_DELAY = 2000L
    private val searchRunnable = Runnable { searchTrack() }
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private var searchText: String = ""
    private lateinit var adapter: TrackAdapter
    private lateinit var adapterHistory: TrackAdapter


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


    private fun setElements(status: SearchStatus) {
        if (status == SearchStatus.SUCCESS) {
            binding.searchProgressBar.visibility = View.GONE
            binding.placeholderImage.visibility = View.GONE
            binding.placeholderMessage.visibility = View.GONE
            binding.placeholderUpdateButton.visibility = View.GONE
            binding.trackRecyclerView.visibility = View.VISIBLE
        } else if (status == SearchStatus.CONNECTION_ERROR) {
            binding.searchProgressBar.visibility = View.GONE
            binding.placeholderImage.setImageResource(R.drawable.placeholder_songs_no_connection)
            binding.placeholderMessage.setText(R.string.something_went_wrong)
            binding.placeholderImage.visibility = View.VISIBLE
            binding.placeholderMessage.visibility = View.VISIBLE
            binding.placeholderUpdateButton.visibility = View.VISIBLE
            binding.trackRecyclerView.visibility = View.GONE
        } else if (status == SearchStatus.EMPTY_RESULT) {
            binding.searchProgressBar.visibility = View.GONE
            binding.placeholderImage.setImageResource(R.drawable.placeholder_songs_nothing_found)
            binding.placeholderMessage.setText(R.string.nothing_found)
            binding.placeholderImage.visibility = View.VISIBLE
            binding.placeholderMessage.visibility = View.VISIBLE
            binding.placeholderUpdateButton.visibility = View.GONE
            binding.trackRecyclerView.visibility = View.GONE
        } else if (status == SearchStatus.IN_PROGRESS) {
            binding.searchProgressBar.visibility = View.VISIBLE
            binding.placeholderImage.visibility = View.GONE
            binding.placeholderMessage.visibility = View.GONE
            binding.placeholderUpdateButton.visibility = View.GONE
            binding.trackRecyclerView.visibility = View.GONE
        }
    }


    private fun searchTrack() {

        if (searchText.isNotEmpty()) {
            searchViewModel.searchTrack(searchText)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onPause() {
        super.onPause()
        searchViewModel.setPaused(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        binding.searchLine.isSaveEnabled = false

        binding.clearButton.setOnClickListener {
            searchViewModel.clearSearchText()
            binding.searchLine.setText("")
        }

        searchViewModel.searchStatus.observe(viewLifecycleOwner) {
            setElements(it)
        }

        searchViewModel.getTrackList().observe(viewLifecycleOwner) { trackList ->
            adapter = TrackAdapter({
                onTrackClick(it)
            }, trackList)

            binding.trackRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.trackRecyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        }


        searchViewModel.getTrackHistoryList().observe(viewLifecycleOwner) { trackList ->
            adapterHistory = TrackAdapter({
                onTrackClick(it)
            }, trackList)

            binding.historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.historyRecyclerView.adapter = adapterHistory
            adapterHistory.notifyDataSetChanged()
        }

        searchViewModel.isShowHistoryList.observe(viewLifecycleOwner) {
            if (it) {
                showTrackHistory()
            } else {
                hideTrackHistory()
            }
        }

        searchViewModel.fillHistoryList()

        searchViewModel.selectedTrack.observe(viewLifecycleOwner) { track ->
            track?.let {
                navigateToPlayerActivity(it)
                searchViewModel.onTrackNavigationDone()
            }
        }


        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearButton.visibility = clearButtonVisibility(s)
                searchText = s.toString()
                if (binding.searchLine.hasFocus() && s.isNullOrEmpty()) {
                    searchViewModel.showHistoryList()
                } else {
                    searchViewModel.hideHistoryList()
                }

                searchDebounce()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }

        binding.searchLine.addTextChangedListener(simpleTextWatcher)

        binding.searchLine.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTrack()
            }
            false
        }

        binding.placeholderUpdateButton.setOnClickListener {
            searchTrack()
        }

        binding.clearHistoryButton.setOnClickListener {
            searchViewModel.clearHistory()
            searchViewModel.hideHistoryList()
        }


        binding.searchLine.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && binding.searchLine.text.isEmpty()) {
                searchViewModel.showHistoryList()
            } else {
                searchViewModel.hideHistoryList()
            }
        }

        searchViewModel.setPaused(false)
        binding.searchLine.setText(searchViewModel.getLastQuery())

    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun navigateToPlayerActivity(track: Track) {
        val displayIntent = Intent(context, PlayerActivity::class.java)
        displayIntent.putExtra("track", track)
        startActivity(displayIntent)
    }

    private fun showTrackHistory() {
        if (adapterHistory.itemCount > 0) {
            binding.clearHistoryButton.visibility = View.VISIBLE
            binding.placeholderHistoryText.visibility = View.VISIBLE
            binding.historyRecyclerView.visibility = View.VISIBLE
        }
        binding.trackRecyclerView.visibility = View.GONE
    }

    private fun hideTrackHistory() {
        binding.clearHistoryButton.visibility = View.GONE
        binding.placeholderHistoryText.visibility = View.GONE
        binding.historyRecyclerView.visibility = View.GONE
        binding.trackRecyclerView.visibility = View.VISIBLE
    }

    fun onTrackClick(trackId: Int) {
        if (clickDebounce()) {
            searchViewModel.clickTrack(trackId)
        }
    }

}