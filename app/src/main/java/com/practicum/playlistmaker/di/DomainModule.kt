package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.data.sharing.impl.ExternalNavigatorImpl
import com.practicum.playlistmaker.domain.player.PlayTrackInteractor
import com.practicum.playlistmaker.domain.player.impl.PlayTrackInteractorImpl
import com.practicum.playlistmaker.domain.search.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.search.SearchInteractor
import com.practicum.playlistmaker.domain.search.impl.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.domain.search.impl.SearchInteractorImpl
import com.practicum.playlistmaker.domain.settings.SettingsInteractor
import com.practicum.playlistmaker.domain.settings.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.domain.sharing.ExternalNavigator
import com.practicum.playlistmaker.domain.sharing.SharingInteractor
import com.practicum.playlistmaker.domain.sharing.impl.SharingInteractorImpl
import org.koin.dsl.module


val domainModule = module {

    factory<SearchInteractor> {
        SearchInteractorImpl(get())
    }

    factory<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }

    factory<SharingInteractor> {
        SharingInteractorImpl(get())
    }

    factory<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(get())
    }

    factory<PlayTrackInteractor> {
        PlayTrackInteractorImpl(get())
    }

    factory<ExternalNavigator> {
        ExternalNavigatorImpl(context = get())
    }

}