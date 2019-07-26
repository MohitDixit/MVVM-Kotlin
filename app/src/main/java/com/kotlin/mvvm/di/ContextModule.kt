package com.kotlin.mvvm.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.kotlin.mvvm.ui.main.MainActivityViewModelFactory
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ContextModule(private val context: Context) {
    @Provides
    @Singleton
    fun provideContext(): Context = context

    @Provides
    @Singleton
    fun provideMainActivityViewModelFactory(
        factory: MainActivityViewModelFactory
    ): ViewModelProvider.Factory = factory

}