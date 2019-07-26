package com.kotlin.mvvm.di

import com.kotlin.mvvm.ui.detail.OrderDetailActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector
import com.kotlin.mvvm.ui.main.MainActivity

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector
    abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun bindDescriptionActivity(): OrderDetailActivity
}