package com.practicum.playlistmaker

import android.content.SharedPreferences
import com.practicum.playlistmaker.domain.api.PlayTrackInteractor
import com.practicum.playlistmaker.domain.impl.PlayTrackInteractorImpl
import com.practicum.playlistmaker.data.impl.PlayTrackRepositoryImpl
import com.practicum.playlistmaker.data.impl.SearchHistoryImpl
import com.practicum.playlistmaker.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.impl.SearchHistoryInteractorImpl

object Creator {
    fun providePlayTrackInteractor(): PlayTrackInteractor {
        return PlayTrackInteractorImpl(PlayTrackRepositoryImpl())
    }

    fun provideSearchHistoryInteractor(sharedPrefs: SharedPreferences): SearchHistoryInteractor {
        return  SearchHistoryInteractorImpl(SearchHistoryImpl(sharedPrefs))
    }

}