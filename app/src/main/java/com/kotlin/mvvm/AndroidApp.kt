package com.kotlin.mvvm

import android.app.Activity
import android.app.Application
import com.kotlin.mvvm.di.ContextModule
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import com.kotlin.mvvm.di.DaggerAppComponent
import dagger.android.HasAndroidInjector
import javax.inject.Inject


class AndroidApp : Application(), HasAndroidInjector {

    @Inject
    lateinit var activityDispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun onCreate() {
        super.onCreate()
        DaggerAppComponent.builder()
            .contextModule(ContextModule(this))
            .build()
            .inject(this)
    }

    override fun androidInjector(): AndroidInjector<Any> = activityDispatchingAndroidInjector
}