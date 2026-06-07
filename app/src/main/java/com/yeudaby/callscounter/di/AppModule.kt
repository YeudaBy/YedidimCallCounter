package com.yeudaby.callscounter.di

import com.yeudaby.callscounter.data.AndroidCallsRepositoryImpl
import com.yeudaby.callscounter.data.AndroidUserRepositoryImpl
import com.yeudaby.calls_counter.shared.domain.repository.CallsRepository
import com.yeudaby.calls_counter.shared.domain.repository.UserRepository
import com.yeudaby.callscounter.ui.home.HomeViewModel
import com.yeudaby.callscounter.ui.leaderboard.LeaderboardViewModel
import com.yeudaby.callscounter.ui.settings.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<CallsRepository> { AndroidCallsRepositoryImpl(androidContext()) }
    single<UserRepository> { AndroidUserRepositoryImpl(androidContext()) }
    
    viewModel { HomeViewModel(get(), get()) }
    viewModel { LeaderboardViewModel(get(), get()) }
    viewModel { SettingsViewModel(get()) }
}
