package com.fastnews.di

import com.fastnews.mechanism.VerifyNetworkInfo
import org.koin.dsl.module

val networkModule = module {
    single { VerifyNetworkInfo(get()) }
}