package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.interactors.PlayerInteractorImpl
import org.koin.dsl.module

val PlayerDomainModule = module {
    factory<PlayerInteractor> { PlayerInteractorImpl(get()) }
}
