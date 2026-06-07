package com.yeudaby.callscounter

import android.app.Application
import com.yeudaby.callscounter.di.appModule
import com.yeudaby.calls_counter.shared.di.sharedModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CallsCounterApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidContext(this@CallsCounterApplication)
            modules(sharedModule, appModule)
        }
    }
}
