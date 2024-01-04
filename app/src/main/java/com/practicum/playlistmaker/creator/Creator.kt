package com.practicum.playlistmaker.creator

import android.content.Context
import android.content.SharedPreferences
import com.practicum.playlistmaker.data.player.impl.PlayTrackRepositoryImpl
import com.practicum.playlistmaker.data.search.impl.SearchHistoryImpl
import com.practicum.playlistmaker.data.search.impl.SearchRepositoryImpl
import com.practicum.playlistmaker.data.search.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.settings.impl.SettingRepositoryImpl
import com.practicum.playlistmaker.data.sharing.impl.ExternalNavigatorImpl
import com.practicum.playlistmaker.domain.player.PlayTrackInteractor
import com.practicum.playlistmaker.domain.player.impl.PlayTrackInteractorImpl
import com.practicum.playlistmaker.domain.search.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.search.SearchInteractor
import com.practicum.playlistmaker.domain.search.impl.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.domain.search.impl.SearchInteractorImpl
import com.practicum.playlistmaker.domain.settings.SettingInteractor
import com.practicum.playlistmaker.domain.settings.impl.SettingInteractorImpl
import com.practicum.playlistmaker.domain.sharing.SharingInteractor
import com.practicum.playlistmaker.domain.sharing.impl.SharingInteractorImpl

object Creator {
    fun providePlayTrackInteractor(): PlayTrackInteractor {
        return PlayTrackInteractorImpl(PlayTrackRepositoryImpl())
    }

    fun provideSearchHistoryInteractor(sharedPrefs: SharedPreferences): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(SearchHistoryImpl(sharedPrefs))
    }

    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(ExternalNavigatorImpl(context))
    }

    fun provideSettingInteractor(sharedPrefs: SharedPreferences): SettingInteractor {
        return SettingInteractorImpl(SettingRepositoryImpl(sharedPrefs))
    }

    fun provideSearchInteractor(): SearchInteractor {
        return SearchInteractorImpl(SearchRepositoryImpl(RetrofitNetworkClient()))
    }


}