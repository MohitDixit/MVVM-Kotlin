package com.kotlin.mvvm.di

import com.kotlin.mvvm.ui.main.DescriptionActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector
import com.kotlin.mvvm.ui.main.MainActivity
import com.kotlin.mvvm.ui.main.di.MainActivityModule

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = arrayOf(MainActivityModule::class))
    abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = arrayOf(MainActivityModule::class))
    abstract fun bindDescriptionActivity(): DescriptionActivity
}