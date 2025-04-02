package com.practicum.playlistmaker.di

import android.media.MediaPlayer
import org.koin.dsl.module

val PlayerDataModule = module {
    factory { MediaPlayer() }
}
