package com.practicum.playlistmaker.data.impl

import android.content.SharedPreferences
import com.practicum.playlistmaker.data.api.SearchHistoryRepository

import com.practicum.playlistmaker.domain.model.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class SearchHistoryImpl(private val sharedPrefs: SharedPreferences): SearchHistoryRepository {
    private val MAX_TRACK_IN_HISTORY: Int = 10
    private val HISTORY_KEY = "historyList"

    var trackHistoryList: MutableList<Track> = ArrayList()

    override fun addTrack(track: Track) {
        val historyList = sharedPrefs.getString(HISTORY_KEY, "")

        if (historyList.isNullOrEmpty()) {
            trackHistoryList.add(track)
        } else {

            val typeToken = object : TypeToken<MutableList<Track>>() {}.type
            trackHistoryList.clear()
            trackHistoryList.addAll(Gson().fromJson<MutableList<Track>>(historyList, typeToken))

            if (trackHistoryList.contains(track)) {
                trackHistoryList.remove(track)
                trackHistoryList.add(0, track)
            } else {
                if (trackHistoryList.size == MAX_TRACK_IN_HISTORY) {
                    trackHistoryList.removeAt(MAX_TRACK_IN_HISTORY - 1)
                    trackHistoryList.add(0, track)
                } else {
                    trackHistoryList.add(0, track)
                }
            }
        }

        sharedPrefs.edit().putString(HISTORY_KEY, Gson().toJson(trackHistoryList)).apply()
    }

    override fun getHistoryList() {
        val historyList = sharedPrefs.getString(HISTORY_KEY, "")
        val typeToken = object : TypeToken<MutableList<Track>>() {}.type
        trackHistoryList.clear()
        if (!historyList.isNullOrEmpty()) {
            val trackList: ArrayList<Track> = Gson().fromJson(historyList, typeToken)
            trackHistoryList.addAll(trackList)
        }
    }

    override fun clearHistory() {
        sharedPrefs.edit().putString(HISTORY_KEY, "").apply()
    }

    override fun clearTrackList() {
        trackHistoryList.clear()
    }

    override fun getTrackList() : MutableList<Track> {
        return trackHistoryList
    }




}