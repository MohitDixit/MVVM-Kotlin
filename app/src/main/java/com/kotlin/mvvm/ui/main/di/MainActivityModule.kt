package com.kotlin.mvvm.ui.main.di

import dagger.Module
import dagger.Provides
import com.kotlin.mvvm.repository.OrderListRepository
import com.kotlin.mvvm.ui.main.MainActivityViewModel
import com.kotlin.mvvm.util.SchedulerProvider

@Module
class MainActivityModule {

    @Provides
    fun provideViewModel(orderListRepository: OrderListRepository, schedulerProvider: SchedulerProvider) = MainActivityViewModel(orderListRepository, schedulerProvider)
}