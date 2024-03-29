package com.kwasowski.sportslife

import android.app.Application
import com.kwasowski.sportslife.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
        initTimber()
    }

    private fun initKoin() {
        startKoin {
            androidContext(this@App)
            androidLogger()
            modules(appModule)
        }
    }

    private fun initTimber() {
        Timber.plant(Timber.DebugTree())
    }
}