package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.models.Resource
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {
    private val executor = Executors.newCachedThreadPool()

    override fun search(expression: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            when (val result = repository.search(expression)) {
                is Resource.Success -> {
                    consumer.consume(result.data, null)
                }
                is Resource.Error -> {
                    consumer.consume(null, result.errorType)
                }
            }
        }
    }
}