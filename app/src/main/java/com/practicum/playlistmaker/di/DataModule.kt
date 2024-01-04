package com.practicum.playlistmaker.di

import com.google.gson.Gson
import com.practicum.playlistmaker.data.player.impl.PlayTrackRepositoryImpl
import com.practicum.playlistmaker.data.search.ITunesApi
import com.practicum.playlistmaker.data.search.impl.SearchHistoryImpl
import com.practicum.playlistmaker.data.search.impl.SearchRepositoryImpl
import com.practicum.playlistmaker.data.search.network.NetworkClient
import com.practicum.playlistmaker.data.search.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.settings.impl.SettingsRepositoryImpl
import com.practicum.playlistmaker.domain.player.PlayTrackRepository
import com.practicum.playlistmaker.domain.search.SearchHistoryRepository
import com.practicum.playlistmaker.domain.search.SearchRepository
import com.practicum.playlistmaker.domain.settings.SettingsRepository
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
    val appleBaseUrl = "https://itunes.apple.com"

    single<ITunesApi> {
        Retrofit.Builder().baseUrl(appleBaseUrl).addConverterFactory(GsonConverterFactory.create())
            .build().create(ITunesApi::class.java)
    }

    factory { Gson() }

    single<NetworkClient> {
        RetrofitNetworkClient(get())
    }


    factory<PlayTrackRepository> {
        PlayTrackRepositoryImpl()
    }

    factory<SearchHistoryRepository> {
        SearchHistoryImpl(context = get())
    }

    factory<SettingsRepository> {
        SettingsRepositoryImpl(context = get())
    }

    factory<SearchRepository> {
        SearchRepositoryImpl(get())
    }

}