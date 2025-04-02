package com.practicum.playlistmaker.di

import com.google.gson.Gson
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.datasource.SearchHistoryDataSource
import com.practicum.playlistmaker.search.data.datasource.SharedPreferencesSearchHistoryDataSource
import com.practicum.playlistmaker.search.data.dto.SearchHistoryDto
import com.practicum.playlistmaker.search.data.network.ITunesSearchAPI
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val SearchDataModule = module {

    factory { Gson() }

    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<ITunesSearchAPI> {
        get<Retrofit>().create(ITunesSearchAPI::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get())
    }

    factory { SearchHistoryDto(get()) }

    single {
        androidContext().getSharedPreferences("search_history", android.content.Context.MODE_PRIVATE)
    }

    single<SearchHistoryDataSource> {
        SharedPreferencesSearchHistoryDataSource(get())
    }


}
