package com.example.mycode_lm_ng.ui.main.di

import dagger.Module
import dagger.Provides
import com.example.mycode_lm_ng.repository.Repository
import com.example.mycode_lm_ng.ui.main.MainActivityViewModel
import com.example.mycode_lm_ng.util.SchedulerProvider

@Module
class MainActivityModule {

    @Provides
    fun provideViewModel(repository: Repository, schedulerProvider: SchedulerProvider) = MainActivityViewModel(repository, schedulerProvider)
}