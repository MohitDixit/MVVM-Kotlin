package com.example.mycode_lm_ng.di

import com.example.mycode_lm_ng.ui.main.DescriptionActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector
import com.example.mycode_lm_ng.ui.main.MainActivity
import com.example.mycode_lm_ng.ui.main.di.MainActivityModule

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = arrayOf(MainActivityModule::class))
    abstract fun bindMainActivity(): MainActivity
    @ContributesAndroidInjector(modules = arrayOf(MainActivityModule::class))
    abstract fun bindDescriptionActivity(): DescriptionActivity
}