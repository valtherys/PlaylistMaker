package com.practicum.playlistmaker.di

import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.text.SimpleDateFormat
import java.util.Locale

val TRACK_TIME_CLIENT = named("track_time")
val utilsModule = module{
    single<SimpleDateFormat>(TRACK_TIME_CLIENT){
        SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        )
    }
}