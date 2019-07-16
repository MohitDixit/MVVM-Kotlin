package com.kotlin.mvvm

import android.app.Activity
import android.app.Application
import com.kotlin.mvvm.di.ContextModule
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import com.kotlin.mvvm.di.DaggerAppComponent
import javax.inject.Inject


class AndroidApp : Application(), HasActivityInjector {

    @Inject
    lateinit var activityDispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()

        DaggerAppComponent.builder()
            .contextModule(ContextModule(this))
            .build()
            .inject(this)
    }

    override fun activityInjector(): AndroidInjector<Activity>? = activityDispatchingAndroidInjector
}