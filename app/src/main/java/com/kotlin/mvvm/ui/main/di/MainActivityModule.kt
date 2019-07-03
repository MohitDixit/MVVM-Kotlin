package com.kotlin.mvvm.ui.main.di

import dagger.Module
import dagger.Provides
import com.kotlin.mvvm.repository.OrderListRepository
import com.kotlin.mvvm.ui.main.MainActivityViewModel

@Module
class MainActivityModule {

    @Provides
    fun provideViewModel(orderListRepository: OrderListRepository) = MainActivityViewModel(orderListRepository)
}