package com.kotlin.mvvm.di

import dagger.Component
import dagger.android.AndroidInjectionModule
import com.kotlin.mvvm.AndroidApp
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidInjectionModule::class, DatabaseModule::class, NetworkModule::class, ActivityBuilder::class])
interface AppComponent {

    fun inject(app: AndroidApp)
}