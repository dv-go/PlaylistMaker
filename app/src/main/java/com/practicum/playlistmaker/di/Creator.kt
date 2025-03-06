package com.practicum.playlistmaker.di

import android.content.Context
import com.practicum.playlistmaker.data.SearchHistory
import com.practicum.playlistmaker.data.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.interactors.TracksInteractorImpl

object Creator {
    private lateinit var searchHistory: SearchHistory

    fun init(context: Context) {
        val sharedPreferences = context.getSharedPreferences("search_prefs", Context.MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPreferences)
    }

    private fun getMoviesRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient)
    }

    fun provideMoviesInteractor(): TracksInteractor {
        return TracksInteractorImpl(getMoviesRepository(), searchHistory)
    }
}
