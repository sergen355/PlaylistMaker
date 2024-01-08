package com.practicum.playlistmaker.ui.search.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.domain.model.SearchStatus
import com.practicum.playlistmaker.domain.model.Track
import com.practicum.playlistmaker.domain.search.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.search.SearchInteractor


class SearchViewModel(
    private val searchInteractor: SearchInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private var _searchStatusMutable = MutableLiveData<SearchStatus>()
    val searchStatus: LiveData<SearchStatus> = _searchStatusMutable

    private var _trackListMutable = MutableLiveData<List<Track>>()
    private var _trackHistoryListMutable = MutableLiveData<List<Track>>()


    private var _isShowHistoryListMutable = MutableLiveData<Boolean>()
    val isShowHistoryList: LiveData<Boolean> = _isShowHistoryListMutable

    private var isPaused: Boolean = false
    private var lastQuery: String = ""

    fun getLastQuery():String {
        return lastQuery
    }

    fun setPaused(arg: Boolean) {
        isPaused = arg
    }

    private val _selectedTrack = MutableLiveData<Track?>()
    val selectedTrack: LiveData<Track?> = _selectedTrack


    fun getTrackHistoryList(): LiveData<List<Track>> {
        return _trackHistoryListMutable
    }

    fun getTrackList(): LiveData<List<Track>> {
        return _trackListMutable
    }

    fun fillHistoryList() {
        searchHistoryInteractor.getHistoryList()
        _trackHistoryListMutable.value = searchHistoryInteractor.getTrackList()
    }

    fun clearSearchText() {
        _trackListMutable.value = listOf()
        _searchStatusMutable.value = SearchStatus.SUCCESS

    }

    fun showHistoryList() {
        _trackListMutable.value = listOf()
        searchHistoryInteractor.getHistoryList()
        _trackHistoryListMutable.value = searchHistoryInteractor.getTrackList()
        _searchStatusMutable.value = SearchStatus.SUCCESS
        _isShowHistoryListMutable.value = true
    }

    fun hideHistoryList() {
        _trackHistoryListMutable.value = listOf()
        _isShowHistoryListMutable.value = false
    }

    fun clearHistory() {
        searchHistoryInteractor.clearHistory()
        _trackHistoryListMutable.value = listOf()
    }

    fun clickTrack(trackId: Int) {

        var track = _trackListMutable.value?.find { it.trackId == trackId }
        if (track != null) {
            searchHistoryInteractor.addTrack(track)
        } else {
            track =
                searchHistoryInteractor.getTrackList().find {
                    it.trackId == trackId
                }
        }
        _selectedTrack.value = track

    }

    fun onTrackNavigationDone() {
        _selectedTrack.value = null
    }

    fun searchTrack(queryString: String) {
        if (!isPaused and !queryString.equals(lastQuery)) {
            _searchStatusMutable.value = SearchStatus.IN_PROGRESS
            val resp = searchInteractor.searchTrack(queryString, object :
                SearchInteractor.TrackConsumer {
                override fun consume(foundTracks: List<Track>?) {
                    if (foundTracks == null) {
                        _searchStatusMutable.postValue(SearchStatus.CONNECTION_ERROR)
                    }
                    if (foundTracks != null) {
                        if (foundTracks.isEmpty()) {
                            _searchStatusMutable.postValue(SearchStatus.EMPTY_RESULT)
                        } else if (foundTracks.isNotEmpty()) {
                            _trackListMutable.postValue(foundTracks!!)
                            _searchStatusMutable.postValue(SearchStatus.SUCCESS)
                        }
                    }
                }
            })
            lastQuery = queryString
        }
    }

}