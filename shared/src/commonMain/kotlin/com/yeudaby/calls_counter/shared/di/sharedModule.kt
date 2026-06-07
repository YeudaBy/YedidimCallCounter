package com.yeudaby.calls_counter.shared.di

import com.yeudaby.calls_counter.shared.domain.usecase.AnalyzeCallInsightsUseCase
import org.koin.dsl.module

val sharedModule = module {
    factory { AnalyzeCallInsightsUseCase() }
}
