package com.fastnews

import android.app.Application
import com.fastnews.di.appModule
import com.fastnews.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FastNewsApp: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@FastNewsApp)
            modules(viewModelModule, appModule)
        }
    }
}