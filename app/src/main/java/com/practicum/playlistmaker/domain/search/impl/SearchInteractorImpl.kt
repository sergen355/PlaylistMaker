package com.practicum.playlistmaker.domain.search.impl

import com.practicum.playlistmaker.domain.search.SearchRepository
import com.practicum.playlistmaker.domain.search.SearchInteractor
import java.util.concurrent.Executors

class SearchInteractorImpl(private val repository: SearchRepository) : SearchInteractor {
    private val executor = Executors.newCachedThreadPool()

    override fun searchTrack(queryString: String, consumer: SearchInteractor.TrackConsumer) {
        executor.execute {
            consumer.consume(repository.searchTrack(queryString))
        }
    }
}