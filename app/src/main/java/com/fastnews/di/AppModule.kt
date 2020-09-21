package com.fastnews.di

import com.fastnews.datasource.TimelineDataSourceFactory
import kotlinx.coroutines.CoroutineScope
import org.koin.dsl.module

val appModule = module {
    factory { provideTimelineDataSourceFactory(get()) }
}

fun provideTimelineDataSourceFactory(scope: CoroutineScope) = TimelineDataSourceFactory(scope)
